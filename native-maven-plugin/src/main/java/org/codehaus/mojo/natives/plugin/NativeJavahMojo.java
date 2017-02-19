package org.codehaus.mojo.natives.plugin;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.mojo.natives.NativeBuildException;
import org.codehaus.mojo.natives.javah.Javah;
import org.codehaus.mojo.natives.javah.JavahConfiguration;
import org.codehaus.mojo.natives.manager.JavahManager;
import org.codehaus.mojo.natives.manager.NoSuchNativeProviderException;
import org.codehaus.plexus.archiver.util.DefaultFileSet;
import org.codehaus.plexus.archiver.zip.ZipArchiver;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

/**
 * Generate JNI include files based on a set of class names
 */
@Mojo(name = "javah", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresDependencyResolution = ResolutionScope.COMPILE)
public class NativeJavahMojo
    extends AbstractNativeMojo
{

    /**
     * Javah Provider.
     * @since 1.0-alpha-2
     */
    @Parameter(defaultValue = "default", required = true)
    private String javahProvider;

    /**
     * List of class names to generate native files. Additional JNI interface will automatically discovered from
     * project's dependencies of <i>jar</i> type, when <i>javahSearchJNIFromDependencies</i> is true
     * @since 1.0-alpha-4
     */
    @Parameter
    private List javahClassNames = new ArrayList( 0 );

    /**
     * Enable the search from project dependencies for JNI interfaces, in addition to <i>javahClassNames</i>
     * @since 1.0-alpha-4
     */
    @Parameter(defaultValue = "false")
    private boolean javahSearchJNIFromDependencies;

    /**
     * Path to javah executable, if present, it will override the default one which bases on architecture type. See
     * 'javahProvider' argument
     * @since 1.0-alpha-2
     */
    @Parameter
    private File javahPath;

    /**
     * Where to place javah generated file
     * @since 1.0-alpha-2
     */
    @Parameter(defaultValue = "${project.build.directory}/native/javah", required = true)
    protected File javahOutputDirectory;

    /**
     * if configured, this value will be combined with outputDirectory to pass into javah's -o option
     * @since 1.0-alpha-4
     */
    @Parameter
    private String javahOutputFileName;

    /**
     * Additional javah classname and its corresponding header name. Use this option to create one class per header
     * <p/>
     *
     * <pre>
     * &lt;javahIncludes&gt;
     *   &lt;javahInclude&gt;
     *     &lt;className&gt;com.some.Class&lt;/className&gt;
     *     &lt;headerName&gt;Class.h&lt;/headerName&gt;
     *   &lt;javahInclude&gt;
     * &lt;/javahIncludes&gt;
     * </pre>
     * @since 1.0-alpha-8
     */
    @Parameter
    private List javahIncludes = new ArrayList();

    /**
     * Enable javah verbose mode
     * @since 1.0-alpha-2
     */
    @Parameter(defaultValue = "false")
    private boolean javahVerbose;

    /**
     * Archive all generated include files and deploy as an inczip
     */
    @Parameter(defaultValue = "false")
    private boolean attach;

    /**
     * Classifier name when install/deploy generated includes file. See ${attach} for details
     */
    @Parameter(defaultValue = "javah")
    private String classifier;

    /**
     * Archive file to bundle all generated include files if enable by ${attach}
     * @since 1.0-alpha-8
     */
    @Parameter(defaultValue = "${project.build.directory}/${project.build.finalName}.inczip", required = true)
    private File incZipFile;

    /**
     * Set CLASSPATH env variable instead of using -classpath command-line argument. Use this option to allow large
     * number of jars in classpath due to command line size limit under Windows
     * @since 1.0-alpha-9
     */
    @Parameter(defaultValue = "false")
    private boolean useEnvClasspath;

    /**
     * Internal: To look up javah implementation
     * @since 1.0-alpha-2
     */

    @Component
    private JavahManager manager;

    /**
     * Maven ProjectHelper.
     * @since 1.0-alpha-8
     */
    @Component
    private MavenProjectHelper projectHelper;

    /**
     * For unit test only
     */
    private JavahConfiguration config;

    public void execute()
        throws MojoExecutionException
    {

        this.discoverAdditionalJNIClassName();

        if ( this.javahClassNames.size() == 0 && this.javahIncludes.size() == 0 )
        {
            return;
        }

        try
        {
            if ( this.javahClassNames.size() != 0 )
            {
                this.config =
                    this.createProviderConfiguration( (String[]) javahClassNames.toArray( new String[javahClassNames.size()] ),
                                                      this.javahOutputFileName );
                this.getJavah().compile( config );
            }

            for ( int i = 0; i < this.javahIncludes.size(); ++i )
            {
                JavahInclude javahInclude = (JavahInclude) this.javahIncludes.get( i );
                this.config =
                    this.createProviderConfiguration( new String[] { javahInclude.getClassName() },
                                                      javahInclude.getHeaderName() );
                this.getJavah().compile( config );
            }

            if ( this.attach )
            {
                attachGeneratedIncludeFilesAsIncZip();
            }
        }
        catch ( NativeBuildException e )
        {
            throw new MojoExecutionException( "Error running javah command", e );
        }

        this.project.addCompileSourceRoot( this.javahOutputDirectory.getAbsolutePath() );

    }

    private void attachGeneratedIncludeFilesAsIncZip()
        throws MojoExecutionException
    {
        try
        {
            ZipArchiver archiver = new ZipArchiver();
            DefaultFileSet fileSet = new DefaultFileSet();
            fileSet.setUsingDefaultExcludes( true );
            fileSet.setDirectory( javahOutputDirectory );
            archiver.addFileSet( fileSet );
            archiver.setDestFile( this.incZipFile );
            archiver.createArchive();

            if ( StringUtils.isBlank( this.classifier ) )
            {
                projectHelper.attachArtifact( this.project, INCZIP_TYPE, null, this.incZipFile );
            }
            else
            {
                projectHelper.attachArtifact( this.project, INCZIP_TYPE, this.classifier, this.incZipFile );
            }
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Unable to archive/deploy generated include files", e );
        }
    }

    private Javah getJavah()
        throws MojoExecutionException
    {
        Javah javah;

        try
        {
            javah = this.manager.getJavah( this.javahProvider );

        }
        catch ( NoSuchNativeProviderException pe )
        {
            throw new MojoExecutionException( pe.getMessage() );
        }

        return javah;
    }

    /**
     * Get all jars in the pom excluding transitive, test, and provided scope dependencies.
     *
     * @return
     */
    private List getJavahArtifacts()
    {
        List list = new ArrayList();

        List artifacts = this.project.getCompileArtifacts();

        if ( artifacts != null )
        {

            for ( Iterator iter = artifacts.iterator(); iter.hasNext(); )
            {
                Artifact artifact = (Artifact) iter.next();

                // pick up only jar files
                if ( !"jar".equals( artifact.getType() ) )
                {
                    continue;
                }

                // exclude some other scopes
                if ( Artifact.SCOPE_PROVIDED.equals( artifact.getScope() ) )
                {
                    continue;
                }

                list.add( artifact );

            }
        }

        return list;
    }

    /**
     * Build classpaths from dependent jars including project output directory (i.e. classes directory )
     *
     * @return
     */
    private String[] getJavahClassPath()
    {
        List artifacts = this.getJavahArtifacts();

        String[] classPaths = new String[artifacts.size() + 1];

        classPaths[0] = this.project.getBuild().getOutputDirectory();

        Iterator iter = artifacts.iterator();

        for ( int i = 1; i < classPaths.length; ++i )
        {
            Artifact artifact = (Artifact) iter.next();

            classPaths[i] = artifact.getFile().getPath();
        }

        return classPaths;
    }

    /**
     * Get applicable class names to be "javahed"
     */

    private void discoverAdditionalJNIClassName()
        throws MojoExecutionException
    {
        if ( !this.javahSearchJNIFromDependencies )
        {
            return;
        }

        // scan the immediate dependency list for jni classes

        List artifacts = this.getJavahArtifacts();

        for ( Iterator iter = artifacts.iterator(); iter.hasNext(); )
        {
            Artifact artifact = (Artifact) iter.next();

            this.getLog().info( "Parsing " + artifact.getFile() + " for native classes." );

            try
            {
                ZipFile zipFile = new ZipFile( artifact.getFile() );
                Enumeration zipEntries = zipFile.entries();

                while ( zipEntries.hasMoreElements() )
                {
                    ZipEntry zipEntry = (ZipEntry) zipEntries.nextElement();

                    if ( "class".equals( FileUtils.extension( zipEntry.getName() ) ) )
                    {
                        ClassParser parser = new ClassParser( artifact.getFile().getPath(), zipEntry.getName() );

                        JavaClass clazz = parser.parse();

                        Method[] methods = clazz.getMethods();

                        for ( int j = 0; j < methods.length; ++j )
                        {
                            if ( methods[j].isNative() )
                            {
                                javahClassNames.add( clazz.getClassName() );

                                this.getLog().info( "Found native class: " + clazz.getClassName() );

                                break;
                            }
                        }
                    }
                }// endwhile

                // not full proof
                zipFile.close();
            }
            catch ( IOException ioe )
            {
                throw new MojoExecutionException( "Error searching for native class in " + artifact.getFile(), ioe );
            }
        }

    }

    private JavahConfiguration createProviderConfiguration( String[] classNames, String javahOutputFileName )
        throws MojoExecutionException
    {
        JavahConfiguration config = new JavahConfiguration();
        config.setWorkingDirectory( this.workingDirectory );
        config.setVerbose( this.javahVerbose );
        config.setOutputDirectory( this.javahOutputDirectory );
        config.setFileName( javahOutputFileName );
        config.setClassPaths( this.getJavahClassPath() );
        config.setUseEnvClasspath( useEnvClasspath );
        config.setClassNames( classNames );
        config.setJavahPath( this.javahPath );

        return config;
    }

    /**
     * Internal only for test harness purpose
     *
     * @return
     */
    protected JavahConfiguration getJavahConfiguration()
    {
        return this.config;
    }

    /**
     * Internal for unit test only
     */

    protected MavenProject getProject()
    {
        return this.project;
    }
}

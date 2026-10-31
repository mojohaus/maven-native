/*
 * The MIT License
 *
 * Copyright (c) MojoHaus and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

// Runs a built executable and returns its stdout; an optional library path stands in for rpath
def run = { File exe, List<File> libDirs ->
    def pb = new ProcessBuilder(exe.absolutePath).directory(exe.parentFile).redirectErrorStream(true)
    if (libDirs) {
        pb.environment().put('LD_LIBRARY_PATH', libDirs*.absolutePath.join(File.pathSeparator))
    }
    def process = pb.start()
    def out = process.inputStream.text
    assert process.waitFor() == 0 : "${exe.name} exited non-zero: ${out}"
    return out.trim()
}

// Archive members as listed by ar
def members = { File archive ->
    def process = new ProcessBuilder('ar', 't', archive.absolutePath).redirectErrorStream(true).start()
    def out = process.inputStream.text
    assert process.waitFor() == 0 : "ar t ${archive} failed: ${out}"
    return out.readLines()*.trim()
}

def isElf = { File f -> f.bytes.length > 4 && f.bytes[0] == 0x7f && f.bytes[1] == (byte) 'E' && f.bytes[2] == (byte) 'L' && f.bytes[3] == (byte) 'F' }
def contains = { File f, String needle -> new String(f.bytes, 'ISO-8859-1').contains(needle) }

def linux = new File(basedir, '../linkages/linux-x86')

// static linkage: libraries are archives, the executable embeds their code
def libFuncStatic = new File(linux, 'libFuncStatic/target/libFuncStatic.a')
def libSubFuncStatic = new File(linux, 'libSubFuncStatic/target/libSubFuncStatic.a')
assert libFuncStatic.isFile() : "missing ${libFuncStatic}"
assert libSubFuncStatic.isFile() : "missing ${libSubFuncStatic}"
assert members(libFuncStatic) == ['helloFunc.o']
assert members(libSubFuncStatic) == ['helloSubFunc.o']
assert new File(linux, 'libFuncStatic/target/libFuncStatic.inczip').isFile()
assert new File(linux, 'libSubFuncStatic/target/libSubFuncStatic.inczip').isFile()

def exeStatic = new File(linux, 'ExeStatic/target/ExeStatic.uexe')
assert exeStatic.isFile() && isElf(exeStatic) : "ExeStatic.uexe is not an ELF executable"
assert exeStatic.canExecute()
assert !contains(exeStatic, 'libFuncStatic') : "ExeStatic.uexe must not reference a shared library"
assert run(exeStatic, []) == 'Hello From helloSubFunc'

// dynamic linkage: shared libraries are ELF objects, the executable records them as needed
def libFuncDynamic = new File(linux, 'libFuncDynamic/target/libFuncDynamic.so')
def libSubFuncDynamic = new File(linux, 'libSubFuncDynamic/target/libSubFuncDynamic.so')
assert libFuncDynamic.isFile() && isElf(libFuncDynamic) : "libFuncDynamic.so is not an ELF object"
assert libSubFuncDynamic.isFile() && isElf(libSubFuncDynamic) : "libSubFuncDynamic.so is not an ELF object"

def exeDynamic = new File(linux, 'ExeDynamic/target/ExeDynamic.uexe')
assert exeDynamic.isFile() && isElf(exeDynamic) : "ExeDynamic.uexe is not an ELF executable"
assert contains(exeDynamic, 'libFuncDynamic.so') : "ExeDynamic.uexe does not need libFuncDynamic.so"
assert contains(libFuncDynamic, 'libSubFuncDynamic.so') : "libFuncDynamic.so does not need libSubFuncDynamic.so"
assert run(exeDynamic, [libFuncDynamic.parentFile, libSubFuncDynamic.parentFile]) == 'Hello From helloSubFunc'

// ranlib runs on libSubFuncStatic only
def log = new File(basedir, 'build.log').text
assert log.contains("'ranlib'") : 'native:ranlib did not run'

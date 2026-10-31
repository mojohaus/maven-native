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

def win32 = new File(basedir, '../linkages/win32')

// Files with the given extension in a module's target directory
def files = { String module, String extension ->
    def target = new File(win32, "${module}/target")
    assert target.isDirectory() : "missing ${target}"
    return target.listFiles().findAll { it.isFile() && it.name.endsWith(extension) }
}

// Runs a built executable and returns its stdout; the library directories go on PATH, where Windows looks for DLLs
def run = { File exe, List<File> libDirs ->
    def pb = new ProcessBuilder(exe.absolutePath).directory(exe.parentFile).redirectErrorStream(true)
    def path = (libDirs*.absolutePath + [pb.environment().get('Path') ?: pb.environment().get('PATH') ?: '']).join(File.pathSeparator)
    pb.environment().put('Path', path)
    def process = pb.start()
    def out = process.inputStream.text
    assert process.waitFor() == 0 : "${exe.name} exited non-zero: ${out}"
    return out.trim()
}

// static linkage: the libraries are .lib archives, the executable embeds their code
assert files('FuncStatic', '.lib').size() == 1
assert files('SubFuncStatic', '.lib').size() == 1
def exeStatic = files('ExeStatic', '.exe')
assert exeStatic.size() == 1 : 'ExeStatic.exe was not built'
assert run(exeStatic[0], []) == 'Hello From helloSubFunc'

// dynamic linkage: each library is a DLL with an import library, the executable finds the DLLs on PATH
def funcDll = files('FuncDynamic', '.dll')
def subFuncDll = files('SubFuncDynamic', '.dll')
assert funcDll.size() == 1 : 'FuncDynamic.dll was not built'
assert subFuncDll.size() == 1 : 'SubFuncDynamic.dll was not built'
assert files('FuncDynamic', '.lib').size() == 1 : 'FuncDynamic has no import library'
assert files('SubFuncDynamic', '.lib').size() == 1 : 'SubFuncDynamic has no import library'
def exeDynamic = files('ExeDynamic', '.exe')
assert exeDynamic.size() == 1 : 'ExeDynamic.exe was not built'
assert run(exeDynamic[0], [funcDll[0].parentFile, subFuncDll[0].parentFile]) == 'Hello From helloSubFunc'

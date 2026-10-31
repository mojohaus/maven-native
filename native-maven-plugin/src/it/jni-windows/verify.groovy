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

import groovy.xml.XmlSlurper

def javaModule = new File(basedir, '../jni/java')
def nativeModule = new File(basedir, '../jni/native/win32')

// javac -h wrote the header the C source includes
def header = new File(javaModule, 'target/native/javah/HelloWorld.h')
assert header.isFile() : "missing ${header}"
assert header.text.contains('Java_HelloWorld_sayHello') : 'HelloWorld.h does not declare the native method'

// the DLL exports the function the header declares
def lib = new File(nativeModule, 'target/hello.dll')
assert lib.isFile() : "missing ${lib}"
def bytes = lib.bytes
assert bytes.length > 2 && bytes[0] == (byte) 'M' && bytes[1] == (byte) 'Z' : 'hello.dll is not a PE image'
assert new String(bytes, 'ISO-8859-1').contains('Java_HelloWorld_sayHello') : 'hello.dll does not export Java_HelloWorld_sayHello'

// the test loaded the library through System.loadLibrary and called the native method
def report = new File(nativeModule, 'target/surefire-reports/TEST-HelloWorldTest.xml')
assert report.isFile() : "missing ${report}"
def suite = new XmlSlurper().parse(report)
assert suite.@tests.text() == '1' : "expected one test, got ${suite.@tests.text()}"
assert suite.@failures.text() == '0' && suite.@errors.text() == '0' && suite.@skipped.text() == '0'
assert suite.testcase.@name.text() == 'testNativeHelloWorld'

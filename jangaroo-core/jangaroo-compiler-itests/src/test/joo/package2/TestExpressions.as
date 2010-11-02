/*
 * Copyright 2008 CoreMedia AG
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either 
 * express or implied. See the License for the specific language 
 * governing permissions and limitations under the License.
 */

package package2 {

public class TestExpressions {

  public function TestExpressions() {
  }

  public function testCommaExprAsArrayIndex(foo:String):String {
    var a : Array = [];
    a[1,0] = foo; // commaExpr as array index makes no sense, but is allowed anyway.
    return a[0];
  }

  public function testParenExpr(n:int):int {
    return (n+1)*2;
  }

  public function testStringLiteralsDQ():String {
    return "'€\\\b\t\n\f\r\'/'\xc6\u01Bfe\"'";
  }

  public function testStringLiteralsSQ():String {
    return '"€\\\b\t\n\f\r\'/"\xc6\u01Bfe\'"';
  }

  public function testStringLiterals3():String {
    return "ActionScript <span class='heavy'>3.0</span>";
  }

  public function testStringLiterals4():String {
    return '<item id="155">banana</item>';
  }

  public function testCharLiterals():String {
    return '\'' + '€' + '\\' + '\b' + '\t' + '\n' + '\f' + '\r' + '\'' + '/' + '\'' + '\u00C6' + '\u01bF' + 'e' + '"' + '\'';
  }

  public function testRegexpLiterals():String {
    return [
      "abc".match(/(abc)*/).length,
      " abc abcabcab cabcabc abca bcabcabcabcabcab cabcab cabcabc".match(/(abc)+/g).length,
      "abc".match(/(abc)*/).length,
      "abc\n  abc \nabc abc abc \nabc\n abc ".match(/^abc$/mg).length,
      "=abc\n\tAbc".match(/=(abc)(\s+)(a)/i).length,
      "abc\n\tAbc".match(/c(\s+)a/i).length,
      // note that compc (3+4) does not accept an unquoted / within a character class, as well as earlier Thino versions:
      "/opt/net/jangaroo/$as3-w/o-flash.html".match(/(\w|[_.\-])+([A-Za-z0-9_\/.\-$])+\w+/).length,
     //todo rhino reports invalid quantifier '+': ,"info@jangaroo.net".match(/(\w|[_.\-])+@((\w|-)+\.)+\w{2,4}+/)
      "http://a/b/c/g#s/../x".match(/^(([^:/?#]+):)?(\/\/([^/?#]*))?([^?#]*)(\?([^#]*))?(#(.*))?/).length
      ].join(',');
  }

  public function antitestRegexpLiterals1():int {
    var i:Number = (99 * 2) / 2 / 3;
    return (i + 3) / 2;
  }

  public function antitestRegexpLiterals2(b:int, hi:int, g:*):int {
    // keep that line feed after b please:
    var i:int = b
            /hi/g.p;
    return i + 1;
  }

  public function antitestRegexpLiterals3():int {
    var i:int = 66;
    i /=2;
    return i;
  }

  public function testObjectLiterals():int {
      var o : * = { x: 123, "y": 456 };
      return o.x + o.y;
    }

  public function testNestedObjectLiterals():int {
    var o : * = { x: { xx: 123 }, "y": {'yy':456}};
    return o.x.xx + o.y.yy;
  }

  public function testObjectLiteralWithFun():int {
    var o : * = { fn: function():int {return 123;}, "y": 456 };
    return o.fn() + o.y;
  }

  public function testObjectLiteralsInBooleanShortcuts(p1 :Object, p2 :Object,i:int):int {
    var o : * = p1 || { x: 123, "y": 0 };
    var o2 : * = p2&&{x:1,"y":456};
    var o3 :* = { x: 11, y: 12 } || 4711;
    return o.x + (o2 ? o2.y : i) + o3.x;
  }

  public function testArrayLiterals1():String {
    return [].join(',');
  }

  public function testArrayLiterals2():String {
    return [1+2-2].join(',');
  }

  public function testArrayLiterals3():String {
    return [1+2-2,2+3-3,3+4-4,4,5,6,7,8,9,0].join(',');
  }

  public function testArrayLiterals4():String {
    return [,1+2-2,2+3-3,,,3+4-4,4,5,6,7,8,9,0,,].join(',');
  }

  public function testBinOpExpr(n:int):int {
    return 1+n/2;
  }

  public function testAssignOpExpr(n:int):int {
    var x:Number= n;
    x/=2;
    return n;
  }

  public function testFunExpr(n:int):Function {
    return function(m:int):int { return n*m; };
  }

  public function testPrefixOpExpr(n:int):int{
    return 1+-n+11;
  }

  public function testPostfixOpExpr(n:int):int {
    var x:int = 1+n--;
    return n+x;
  }

  public function testCond(cond:Boolean, ifTrue :int, ifFalse :int):int {
    return cond ? ifTrue : ifFalse;
  }

  public function testIn(obj:Object, prop:String):Boolean {
    return prop in obj;
  }

  public function testIsPrecedence():Boolean {
    var expected:Object = parseInt("foo");
    var actual:Object = parseInt("bar");
    if (expected is Number &&
      actual is Number &&
      isNaN(Number(expected)) &&
      isNaN(Number(actual))) {
      return true;
    }
    expected = 1;
    return false;
  }
}
}
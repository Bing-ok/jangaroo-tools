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

package package1 /*blubber*/ {

/**
* a comment
*/
public class TestMethodCall /* blub ber *//*extends Object*/ {

  public function TestMethodCall() {
  }

  var s:TestMethodCall;

  static public function s(x :int) :int {
    return x;
  }

  public function m(x :int) :int {
    return x;
  }

  protected function prot(x :int) :int {
    return x+1;
  }

  private function priv(x :int) :int {
    return x+2;
  }

  public function callm(x :int, t:TestMethodCall) :int {
    return t.m(x);
  }

  public function callmViaThis(x :int, t:TestMethodCall) :int {
    return this.m(x);
  }

  public function callmViaObject(x :int, t:TestMethodCall) :int {
    var o:* = { t: t };
    return o.t.m(x);
  }

  public function callmViaVar(x :int, t:TestMethodCall) :int {
    var o:TestMethodCall = t;
    return o.m(x);
  }

  public function callmViaField(x :int, t:TestMethodCall) :int {
    s = t;
    return s.m(x);
  }

  public function callmViaThisDotField(x :int, t:TestMethodCall) :int {
    s = t;
    return this.s.m(x);
  }

  public function callProt(x :int, t:TestMethodCall) :int {
    return t.prot(x);
  }

  public function callProtViaThis(x :int, t:TestMethodCall) :int {
    return this.prot(x);
  }

  public function callProtViaObject(x :int, t:TestMethodCall) :int {
    var o:* = { t: t };
    return o.t.prot(x);
  }

  public function callProtViaVar(x :int, t:TestMethodCall) :int {
    var o:TestMethodCall = t;
    return o.prot(x);
  }

  public function callProtViaField(x :int, t:TestMethodCall) :int {
    s = t;
    return s.prot(x);
  }

  public function callProtViaThisDotField(x :int, t:TestMethodCall) :int {
    s = t;
    return this.s.prot(x);
  }

  public function callPriv(x :int, t:TestMethodCall) :int {
    return t.priv(x);
  }

  public function callPrivViaThis(x :int, t:TestMethodCall) :int {
    return this.priv(x);
  }

  public function callPrivViaObject(x :int, t:TestMethodCall) :int {
    var o:* = { t: t };
    return o.t.priv(x);
  }

  public function callPrivViaVar(x :int, t:TestMethodCall) :int {
    var o:TestMethodCall = t;
    return o.priv(x);
  }

  public function callPrivViaField(x :int, t:TestMethodCall) :int {
    s = t;
    return s.priv(x);
  }

  public function callPrivViaThisDotField(x :int, t:TestMethodCall) :int {
    s = t;
    return this.s.priv(x);
  }


}
}
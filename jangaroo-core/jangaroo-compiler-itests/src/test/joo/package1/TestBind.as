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

package package1 {

public class TestBind {

  public function TestBind(state : String) {
    this.state = state;
  }

  public function getState() : String {
    return state;
  }

  public function getState1() : String {
    return state;
  }

  public function getState2() : String {
    return state;
  }

  public function getState3() : String {
    return state;
  }

  public function getState4() : String {
    return state;
  }

  public function getState5() : String {
    return state;
  }

  private function getStatePrivate() : String {
    return state;
  }

  public function testInvokeLocalVar() : * {
    var f : Function = getState1;
    return f();
  }

  public function testInvokeLocalVarUnqualified() : * {
    var f : Function = getState2;
    return f();
  }

  public function testInvokeParameter() : String {
    return invoke(this.getState3);
  }

  public function testInvokeLocalVarMethod() : String {
    var test:TestUnqualifiedAccess = new TestUnqualifiedAccess(state);
    return invoke(test.getPrivateSlot);
  }

  private var test:TestUnqualifiedAccess;
  public function testInvokeFieldMethod() : String {
    test = new TestUnqualifiedAccess(state);
    return invoke(test.getPrivateSlot);
  }

  public function testInvokeFieldMethodThroughLocalFunction() : String {
    function foo():String {
      return getState();
    }
    return foo.call({getState: function():String{return "too bad"}}) as String;
  }

  public function testDontInvokeFieldMethodThroughLocalFunction() : String {
    function foo():String {
      return this.getState();
    }
    return foo.call({getState: function():String{return "sounds good"}}) as String;
  }

  public function testInvokeFieldMethodThroughLocalFunctionExpr() : String {
    var foo :Function = function ():String {
      return getState();
    };
    return foo.call({getState: function():String{return "too bad"}}) as String;
  }

  public function testDontInvokeFieldMethodThroughLocalFunctionExpr() : String {
    var foo :Function = function():String {
      return this.getState();
    };
    return foo.call({getState: function():String{return "sounds good"}}) as String;
  }

  public function testReturn() : String {
    return getGetPrivateSlot()();
  }

  private function getGetPrivateSlot():Function {
    return test.getPrivateSlot;
  }

  public function testInvokeParameterUnqualified() : String {
    return invoke(getState4);
  }

  public function testInvokeObjectField() : String {
    var o:* = {
      getState5: getState5
    };
    return invoke(o.getState5);
  }

  public function testInvokeParameterUnqualifiedPrivate() : String {
    return invoke(getStatePrivate);
  }

  public function testLocalFunctionIde() : int {
    var f:Function = function g(i:int):int {
      if (i == 0) return 0;
      return i + g(i - 1);
    };
    return f(10);
  }

  public function testLocalFunction() : String {
    return invoke(function():String {
      return getState();
    });
  }

  public function testLocalFunctionUnqualified() : String {
    return invoke(function():String {
      return getState();
    });
  }

  // negative tests>
  // This method is never actually called, but only analyzed by the compiler.
  // These expressions must *not* lead to testNotBound being bound!
  public function testDelete() : String {
    //delete this.testNotBound; illegal in AS3, its non-dynamic
    return typeof this.testNotBound;
  }

  // negative test: testNotBound must not be bound!
  public function testNotBound() : String {
    return this.getState();
  }

  // negative test: functions using "this" inside static code must not be bound:
  public static function testStaticNotBound() : Function {
    return function() : Object {
      return this;
    };
  }

  private function invoke(f : Function) : * {
    return f();
  }

  //todo native functions dont validate in Flex/Flash
  //public native function doesNotExist():void;

  public function testBindMethodInBinaryOpExpr():Boolean {
    var gS:Function = this.getState;
    return gS === this.getState;
  }

  /*
  //todo re-write this test to pass in Flex?!
  public function testBindNonExistentMethod() : Function {
    return doesNotExist;
  }*/

  public function testBindTwiceReturnsSameFunction():Boolean {
    return this.getState === this.getState;
  }

  private var state : String;

}
}


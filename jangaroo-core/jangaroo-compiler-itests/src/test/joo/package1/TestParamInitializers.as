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
public class TestParamInitializers /* blub ber *//*extends Object*/ {

  public function TestParamInitializers() {
  }

  public function initParams1( a : String, b : Number = 1) : String {
    return a + "/" + b;
  }

  public static const DEFAULT_FOR_C : Number = 3;

  public function initParams2( a : String = "bar", b : String = "b", c = DEFAULT_FOR_C) : String { //todo reset to b = a? flex compc complains unknown initializer
    return a + "/" + b + "/" + c;
  }

  public function initParams3( a : String, b : String = "foo", ...rest) : String {
    return a + "/" + b + "/" + rest.length;
  }

  public function initParams4( a : String = undefined, b : String = "foo", c : String = undefined, ...rest) : String {
    return a + "/" + b + "/" + c + "/" + rest.length;
  }

  public function testParamWithAnyTypeWithoutWhitespaceSyntax(a:*=99):* {
    // '*=' in typed parameter initializers must not be mistaken as a single token and raise a syntax error!
    // in contrast, in the following line, '*=' is a single token:
    a*=5;
    return a;
  }

  public function testParamWithAnyTypeWithoutWhitespaceSyntax2(a: *=88):int {
    // just a syntax test: '*=' in typed parameter initializers must not be mistaken as a single token and raise a syntax error!
    // in contrast, in the following line, '*=' is a single token:
    a*=6;
    return a;
  }
}
}
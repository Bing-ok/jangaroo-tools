define("as3/package1/uninitializedPackageGlobal",["module","exports","as3-rt/AS3"], function($module,$exports,AS3) { AS3.compilationUnit($module,$exports,function($primaryDeclaration){/*package package1 {

import package1.someOtherPackage.SomeOtherClass;

// This comment to vanish in API
/**
 * Some package-global documentation;
 * /
public*/ var uninitializedPackageGlobal/*:SomeOtherClass*/=null; $primaryDeclaration({value: uninitializedPackageGlobal,writable: true});/*

}
*/
  });
});
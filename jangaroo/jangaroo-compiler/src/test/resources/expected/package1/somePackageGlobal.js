Ext.define("package1.somePackageGlobal", function(somePackageGlobal) {/*package package1 {

import package1.someOtherPackage.SomeOtherClass;

// This comment to vanish in API
[Lazy]
/**
 * Some package-global documentation;
 * /
public var somePackageGlobal:SomeOtherClass
  =*/function somePackageGlobal_(){return( new package1.someOtherPackage.SomeOtherClass());}/*;

}

============================================== Jangaroo part ==============================================*/
    return {
      __lazyFactory__: somePackageGlobal_,
      requires: ["package1.someOtherPackage.SomeOtherClass"]
    };
});

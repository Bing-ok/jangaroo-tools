Ext.define("package1.mxml.pkg.TestComponent", function(TestComponent) {/*package package1.mxml.pkg{
import package1.mxml.pkg.*;
import net.jangaroo.ext.Exml;
public class TestComponent extends TestComponentBase{

    public*/function TestComponent$(config/*:TestComponent = null*/){if(arguments.length<=0)config=null;
    var config_$1/*: package1.mxml.pkg.TestComponentBase*/ =AS3.cast(package1.mxml.pkg.TestComponentBase,{});
    var defaults_$1/*:TestComponent*/ =AS3.cast(TestComponent,{});
    AS3.setBindable(defaults_$1,"property_1" , "withDefault");
    config = net.jangaroo.ext.Exml.apply(defaults_$1,config);
    config_$1.emptyText =net.jangaroo.ext.Exml.asString( '<div class=\'widget-content-list-empty\'>' + package1.mxml.pkg.TestComponentBase.DEFAULT + '</div>');
    config_$1.letters = [
              'a',
              'b',
              'c'
             ];
    net.jangaroo.ext.Exml.apply(config_$1,config);
    this.super$6zW8(config_$1);
  }/*

    [Bindable]
    public var property_1:String;


    [Bindable]
    public var property_2:int;}}

============================================== Jangaroo part ==============================================*/
    return {
      extend: "package1.mxml.pkg.TestComponentBase",
      constructor: TestComponent$,
      super$6zW8: function() {
        package1.mxml.pkg.TestComponentBase.prototype.constructor.apply(this, arguments);
      },
      config: {
        property_1: null,
        property_2: 0
      },
      requires: ["package1.mxml.pkg.TestComponentBase"],
      uses: ["net.jangaroo.ext.Exml"]
    };
});

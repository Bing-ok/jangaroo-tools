package package1 {

import package1.someOtherPackage.SomeOtherClass;

public class ChainedConstants {
  public static const METHOD_TYPE_GET : String = "get";

  public static const DEFAULT_METHOD_TYPE : String = ChainedConstants.METHOD_TYPE_GET;

  public static const THE_METHOD_TYPE : String = METHOD_TYPE_GET;

  public static const ANOTHER_METHOD_TYPE : String = METHOD_TYPE_GET.substr(0, 1);

  public static const THE_BLA : int = SomeOtherClass.BLA;
}
}
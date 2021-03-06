package net.jangaroo.jooc.backend;

import net.jangaroo.jooc.Jooc;
import net.jangaroo.jooc.model.ActionScriptModel;
import net.jangaroo.jooc.model.AnnotatedModel;
import net.jangaroo.jooc.model.AnnotationModel;
import net.jangaroo.jooc.model.AnnotationPropertyModel;
import net.jangaroo.jooc.model.ClassModel;
import net.jangaroo.jooc.model.CompilationUnitModel;
import net.jangaroo.jooc.model.FieldModel;
import net.jangaroo.jooc.model.MemberModel;
import net.jangaroo.jooc.model.MethodModel;
import net.jangaroo.jooc.model.ModelVisitor;
import net.jangaroo.jooc.model.NamespaceModel;
import net.jangaroo.jooc.model.NamespacedModel;
import net.jangaroo.jooc.model.ParamModel;
import net.jangaroo.jooc.model.PropertyModel;
import net.jangaroo.jooc.model.ReturnModel;
import net.jangaroo.jooc.model.TypedModel;
import net.jangaroo.jooc.model.ValuedModel;
import net.jangaroo.utils.CompilerUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A ClassModel visitor that generates ActionScript (API) code.
 */
public class ActionScriptCodeGeneratingModelVisitor implements ModelVisitor {
  public static final Pattern LEADING_ASDOC_WHITESPACE_PATTERN = Pattern.compile("\\s*\\* ?(.*)");
  private final PrintWriter output;
  private CompilationUnitModel compilationUnitModel;
  private boolean skipAsDoc;
  private String indent = "";

  public ActionScriptCodeGeneratingModelVisitor(Writer writer) {
    output = writer instanceof PrintWriter ? (PrintWriter)writer : new PrintWriter(writer);
  }

  public ActionScriptCodeGeneratingModelVisitor(Writer writer, boolean skipAsDoc) {
    this(writer);
    this.skipAsDoc = skipAsDoc;
  }

  public void setCompilationUnitModel(CompilationUnitModel compilationUnitModel) {
    this.compilationUnitModel = compilationUnitModel;
  }

  @Override
  public void visitCompilationUnit(CompilationUnitModel compilationUnitModel) {
    setCompilationUnitModel(compilationUnitModel);
    output.printf("package %s {%n", compilationUnitModel.getPackage());
    indent = "";
    for (String anImport : compilationUnitModel.getImports()) {
      output.printf("import %s;%n", anImport);
    }
    output.println();
    compilationUnitModel.getPrimaryDeclaration().visit(this);
    indent = "";
    output.print("}");
    output.close();
  }

  @Override
  public void visitClass(ClassModel classModel) {
    visitAnnotations(classModel);
    output.print(classModel.getAnnotationCode());
    printAsdoc(classModel.getAsdoc());
    printToken(classModel.getNamespace());
    printTokenIf(classModel.isFinal(), "final");
    printTokenIf(classModel.isDynamic(), "dynamic");
    printToken(classModel.isInterface(), "interface", "class");
    printToken(classModel.getName());
    if (!classModel.isInterface() && !isEmpty(classModel.getSuperclass())) {
      output.printf("extends %s ", classModel.getSuperclass());
    }
    if (!classModel.getInterfaces().isEmpty()) {
      printToken(classModel.isInterface(), "extends", "implements");
      List<String> tokens = classModel.getInterfaces();
      output.print(tokens.get(0));
      for (String token : tokens.subList(1, tokens.size())) {
        output.print(", ");
        output.print(token);
      }
      output.print(" ");
    }
    output.print("{");
    output.print(classModel.getBodyCode());
    indent = "  ";
    for (MemberModel member : classModel.getMembers()) {
      output.println();
      member.visit(this);
    }
    indent = "";
    output.println("}");
  }

  @Override
  public void visitNamespace(NamespaceModel namespaceModel) {
    visitAnnotations(namespaceModel);
    printAsdoc(namespaceModel.getAsdoc());
    printToken(namespaceModel.getNamespace());
    printToken("namespace");
    output.print(namespaceModel.getName());
    generateValue(namespaceModel);
    output.println(";");
  }

  private void visitAnnotations(AnnotatedModel annotatedModel) {
    for (AnnotationModel annotation : annotatedModel.getAnnotations()) {
      annotation.visit(this);
    }
  }

  private void printParameterList(List<? extends ActionScriptModel> models) {
    output.print("(");
    boolean first = true;
    for (ActionScriptModel model : models) {
      if (first) {
        first = false;
      } else {
        output.print(", ");
      }
      model.visit(this);
    }
    output.print(")");
  }

  private void printTokenIf(boolean flag, String token) {
    if (flag) {
      printToken(token);
    }
  }

  private void printToken(boolean flag, String trueToken, String falseToken) {
    printToken(flag ? trueToken : falseToken);
  }

  private void printToken(String token) {
    output.printf("%s ", token);
  }

  private void indent() {
    output.print(indent);
  }

  public void flush() {
    output.flush();
  }

  @Override
  public void visitField(FieldModel fieldModel) {
    visitAnnotations(fieldModel);
    printAsdoc(fieldModel.getAsdoc());
    indent();
    printToken(fieldModel.getNamespace());
    printTokenIf(fieldModel.isStatic(), "static");
    printToken(fieldModel.isConst(), "const", "var");
    output.print(fieldModel.getName());
    generateType(fieldModel);
    generateValue(fieldModel);
    output.println(";");
  }

  @Override
  public void visitProperty(PropertyModel propertyModel) {
    throw new IllegalStateException("PropertyModel should not be visited by code generator.");
  }

  private static List<String> PARAM_SUPPRESSING_ASDOC_TAGS = Arrays.asList("@inheritDoc", "@private");

  @Override
  public void visitMethod(MethodModel methodModel) {
    visitAnnotations(methodModel);
    StringBuilder asdoc = new StringBuilder();
    if (methodModel.getAsdoc() != null) {
      asdoc.append(methodModel.getAsdoc());
    }
    ReturnModel returnModel = methodModel.getReturnModel();
    if (!PARAM_SUPPRESSING_ASDOC_TAGS.contains(asdoc.toString())) {
      // Collect parameters with empty documentation text in a separate builder
      // to only append these if one with documentation text follows.
      // ASDoc tool mixes up parameter-to-text mapping if you leave out intermediate parameters.
      StringBuilder emptyParamsASDoc = new StringBuilder();
      for (ParamModel paramModel : methodModel.getParams()) {
        boolean textEmpty = isEmpty(paramModel.getAsdoc());
        String atParamName = "\n@param " + paramModel.getName();
        if (textEmpty) {
          emptyParamsASDoc.append(atParamName);
        } else {
          if (emptyParamsASDoc.length() > 0) {
            asdoc.append(emptyParamsASDoc.toString());
            emptyParamsASDoc.setLength(0);
          }
          asdoc.append(atParamName).append(" ").append(paramModel.getAsdoc());
        }
      }
      if (!"void".equals(returnModel.getType()) && !isEmpty(returnModel.getAsdoc())) {
        asdoc.append("\n@return ").append(returnModel.getAsdoc());
      }
    }
    printAsdoc(asdoc.toString());
    indent();
    printTokenIf(methodModel.isOverride(), "override");
    String methodBody = methodModel.getBody();
    if (!isPrimaryDeclarationAnInterface()) {
      printToken(methodModel.getNamespace());
      printTokenIf(methodModel.isStatic(), "static");
      printTokenIf(methodModel.isFinal(), "final");
      printTokenIf(methodBody == null, "native");
    }
    printToken("function");
    if (methodModel.getMethodType() != null) {
      printToken(methodModel.getMethodType().toString());
    }
    output.print(methodModel.getName());
    printParameterList(methodModel.getParams());
    returnModel.visit(this);
    if (methodBody != null) {
      output.printf(" {%n    %s%n  }%n", methodModel.getBody());
    } else {
      output.println(";");
    }
  }

  @Override
  public void visitParam(ParamModel paramModel) {
    if (paramModel.isRest()) {
      output.print("...");
    }
    output.print(paramModel.getName());
    if (!paramModel.isRest()) {
      generateType(paramModel);
      generateValue(paramModel);
    }
  }

  @Override
  public void visitReturn(ReturnModel returnModel) {
    generateType(returnModel);
  }

  @Override
  public void visitAnnotation(AnnotationModel annotationModel) {
    printAsdoc(annotationModel.getAsdoc());
    indent(); output.print("[" + annotationModel.getName());
    if (!annotationModel.getProperties().isEmpty()) {
      printParameterList(annotationModel.getProperties());
    }
    output.println("]");
  }

  @Override
  public void visitAnnotationProperty(AnnotationPropertyModel annotationPropertyModel) {
    String name = annotationPropertyModel.getName();
    String value = annotationPropertyModel.getValue();
    String unquoted = CompilerUtils.unquote(value);
    if (unquoted != null) {
      String quote = value.substring(0, 1);
      // escape "<" and "'" (single quote), or asdoc tool will fail:
      value = quote + unquoted.replaceAll("<", "&lt;").replaceAll("'", "&#39;") + quote;
    }
    if (isEmpty(name)) {
      output.print(value);
    } else if (isEmpty(value)) {
      output.print(name);
    } else {
      output.printf("%s = %s", name, value);
    }
  }

  private void printAsdoc(String asdoc) {
    if (!skipAsDoc && asdoc != null && asdoc.trim().length() > 0) {
      indent(); output.println("/**");
      // all @see (and @eventType) lines must come last, so collect them and print them after everything else, removing duplicates:
      LinkedHashSet<String> atSeeLines = new LinkedHashSet<>();
      for (String line : asdoc.trim().split("\n")) {
        Matcher matcher = LEADING_ASDOC_WHITESPACE_PATTERN.matcher(line);
        if (matcher.matches()) {
          line = matcher.group(1);
        }
        // asdoc tool does not like "@" that is not followed by a directive.
        // Thus, we escape all "@"s not following white-space or "*" (so that /**@private*/ still works):
        line = line.replaceAll("([^\\s*{])@", "$1&#64;");
        // also escape "@"s not followed by a letter (cannot be a directive):
        line = line.replaceAll("@([^a-zA-Z])", "&#64;$1");
        String printedLine = indent + " " + ("* " + line).trim();
        if (line.startsWith("@see ") || line.startsWith("@eventType ")) {
          atSeeLines.add(printedLine);
        } else {
          output.println(printedLine);
        }
      }
      for (String atSeeLine : atSeeLines) {
        output.println(atSeeLine);
      }
      output.println(indent + " */");
    }
  }

  private boolean isPrimaryDeclarationAnInterface() {
    return compilationUnitModel.getPrimaryDeclaration() instanceof ClassModel
      && ((ClassModel)compilationUnitModel.getPrimaryDeclaration()).isInterface();
  }

  private void generateType(TypedModel typedModel) {
    if (!isEmpty(typedModel.getType())) {
      output.print(":" + typedModel.getType());
    }
  }

  private void generateValue(ValuedModel valuedModel) {
    if (!isEmpty(valuedModel.getValue())) {
      output.print(" = " + valuedModel.getValue());
    }
  }

  private static boolean isEmpty(String string) {
    return string == null || string.trim().length() == 0;
  }

  public static void main(String[] args) {
    // TODO: move to unit test!
    ClassModel classModel = new ClassModel("com.acme.Foo");
    classModel.setAsdoc("This is the Foo class.");

    AnnotationModel annotation = new AnnotationModel("ExtConfig",
      new AnnotationPropertyModel("target", "'foo.Bar'"));
    classModel.addAnnotation(annotation);

    FieldModel field = new FieldModel("FOOBAR");
    field.setType("String");
    field.setConst(true);
    field.setStatic(true);
    field.setNamespace(NamespacedModel.PRIVATE);
    field.setAsdoc("A constant for foo bar.");
    field.setValue("'foo bar baz'");
    classModel.addMember(field);

    MethodModel method = new MethodModel();
    method.setName("doFoo");
    method.setAsdoc("Some method.");
    method.setBody("trace('foo');");
    ParamModel param = new ParamModel();
    param.setName("foo");
    param.setType("com.acme.sub.Bar");
    param.setValue("null");
    method.setParams(Collections.singletonList(param));
    method.setType("int");
    classModel.addMember(method);

    PropertyModel propertyModel = new PropertyModel();
    propertyModel.setName("baz");
    propertyModel.setType("String");
    propertyModel.setAsdoc("The baz is a string.");
    classModel.addMember(propertyModel);

    StringWriter stringWriter = new StringWriter();
    classModel.visit(new ActionScriptCodeGeneratingModelVisitor(stringWriter));
    System.out.println("Result:\n" + stringWriter);
  }
}

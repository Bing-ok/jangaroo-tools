package net.jangaroo.jooc.mvnplugin.sencha.configurer;

import net.jangaroo.jooc.mvnplugin.Types;
import net.jangaroo.jooc.mvnplugin.sencha.SenchaUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.zip.ZipEntry;
import org.codehaus.plexus.archiver.zip.ZipFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RequiresConfigurer implements Configurer {

  static final String REQUIRES = "requires";
  static final String MAVEN_DEPENDENCY_SCOPE_TEST = "test";

  private MavenProject project;

  public RequiresConfigurer(MavenProject project) {
    this.project = project;
  }

  @Override
  public void configure(Map<String, Object> config) throws MojoExecutionException {
    config.put(REQUIRES, getDependencies());
  }

  private Set<Map<String, Object>> getDependencies() throws MojoExecutionException {
    Set<Map<String, Object>> dependencies = new HashSet<Map<String, Object>>();

    @SuppressWarnings("unchecked") Set<Artifact> dependencyArtifacts = project.getDependencyArtifacts();
    for (Artifact artifact : dependencyArtifacts) {
      if (isSenchaPackageArtifact(artifact)) {
        String senchaPackageNameForArtifact = SenchaUtils.getSenchaPackageNameForArtifact(artifact);
        String version = SenchaUtils.getSenchaVersionForArtifact(artifact);
        if (null == version) {
          throw new MojoExecutionException("Could not determine sencha version from maven version");
        }
        Map<String, Object> require = new HashMap<String, Object>();
        require.put("name", senchaPackageNameForArtifact);
        require.put("version", version);
        dependencies.add(require);
      }
    }

    return dependencies;
  }

  private boolean isSenchaPackageArtifact(Artifact artifact) throws MojoExecutionException {
    boolean result = false;
    String packageName = SenchaUtils.getSenchaPackageNameForArtifact(artifact);
    if (null != packageName
            && Types.JAVASCRIPT_EXTENSION.equals(artifact.getType())
            && !MAVEN_DEPENDENCY_SCOPE_TEST.equalsIgnoreCase(artifact.getScope())) {
      // TODO: remove once everything is ok:
      if ("net.jangaroo__jangaroo-runtime".equals(packageName)
              || "net.jangaroo__ckeditor".equals(packageName)
              || "net.jangaroo__jangaroo-net".equals(packageName)
              || "net.jangaroo__ext-as".equals(packageName)) {
        result = true;
      } else {
        if (null != artifact.getFile()) {
          try {
            ZipFile zipFile = new ZipFile(artifact.getFile());
            ZipEntry zipEntry = zipFile.getEntry(packageName + ".pkg");
            if (zipEntry != null) {
              result = true;
            }
          } catch (IOException e) {
            throw new MojoExecutionException("could not open artifact jar", e);
          }
        }
      }
    }
    return result;
  }

}
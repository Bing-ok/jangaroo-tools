package net.jangaroo.jooc.mvnplugin.sencha;

import net.jangaroo.jooc.mvnplugin.SenchaConfiguration;
import net.jangaroo.jooc.mvnplugin.sencha.configurer.Configurer;
import net.jangaroo.jooc.mvnplugin.sencha.configurer.DefaultSenchaWorkspaceConfigurer;
import net.jangaroo.jooc.mvnplugin.sencha.configurer.LocalPackagesConfigurer;
import net.jangaroo.jooc.mvnplugin.sencha.configurer.PathConfigurer;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.jar.JarArchiver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

class SenchaWorkspaceHelper extends AbstractSenchaHelper {
  private final Configurer[] workspaceConfigurers;

  public SenchaWorkspaceHelper(MavenProject project, SenchaConfiguration senchaConfiguration, Log log) {
    super(project, senchaConfiguration, log);

    PathConfigurer pathConfigurer = new PathConfigurer(senchaConfiguration);
    LocalPackagesConfigurer localPackagesConfigurer = new LocalPackagesConfigurer(project, senchaConfiguration);

    this.workspaceConfigurers = new Configurer[] {
            DefaultSenchaWorkspaceConfigurer.getInstance(),
            pathConfigurer,
            localPackagesConfigurer
    };
  }

  @Override
  public void prepareModule() throws MojoExecutionException {
    if (getSenchaConfiguration().isEnabled()) {
      File workingDirectory = getProject().getBasedir();

      if (null == SenchaUtils.findClosestSenchaWorkspaceDir(workingDirectory.getParentFile())) {
        writeWorkspaceJson(workingDirectory);
      } else {
        getLog().info("Skipping preparation of workspace because there already is a workspace in the directory hierarchy");
      }
    }
  }

  @Override
  public void generateModule() throws MojoExecutionException {
    if (getSenchaConfiguration().isEnabled()) {
      File workingDirectory = getProject().getBasedir();

      if (null == SenchaUtils.findClosestSenchaWorkspaceDir(workingDirectory.getParentFile())) {

        File senchaCfg = new File(workingDirectory.getAbsolutePath() + File.separator + SenchaUtils.SENCHA_WORKSPACE_CONFIG);
        // make sure senchaCfg does not exist
        if (senchaCfg.exists()) {
          if (!senchaCfg.delete()) {
            throw new MojoExecutionException("could not delete " + SenchaUtils.SENCHA_WORKSPACE_CONFIG + " for workspace");
          }
        }

        String line = "sencha generate workspace .";
        CommandLine cmdLine = CommandLine.parse(line);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setWorkingDirectory(workingDirectory);
        executor.setExitValue(0);
        try {
          executor.execute(cmdLine);
        } catch (IOException e) {
          throw new MojoExecutionException("could not execute sencha cmd to generate workspace", e);
        }

        // sencha.cfg should be recreated
        // for normal packages skip generating css and slices
        if (senchaCfg.exists()) {
          PrintWriter pw = null;
          try {
            FileWriter fw = new FileWriter(senchaCfg.getAbsoluteFile(), true);
            pw = new PrintWriter(fw);
            pw.println("ext.dir=" + SenchaUtils.generateAbsolutePathUsingPlaceholder(SenchaConfiguration.Type.WORKSPACE, getSenchaConfiguration().getExtFrameworkDir()));
          } catch (IOException e) {
            throw new MojoExecutionException("could not append ext framework dir to sencha config of workspace");
          } finally {
            if (null != pw) {
              pw.close();
            }
          }
        } else {
          throw new MojoExecutionException("could not find sencha.cfg of workspace");
        }
      } else {
        getLog().info("Skipping generate workspace because there already is a workspace in the directory hierarchy");
      }
    }
  }

  @Override
  public void packageModule(JarArchiver archiver) throws MojoExecutionException {
    // nothing to do
  }

  private void writeWorkspaceJson(File workingDirectory) throws MojoExecutionException {
    Map<String, Object> workspaceConfig = getWorkspaceConfig();

    File fWorkspaceJson = new File(workingDirectory.getAbsolutePath() + File.separator + SenchaUtils.SENCHA_WORKSPACE_FILENAME);
    try {
      SenchaUtils.getObjectMapper().writerWithDefaultPrettyPrinter().writeValue(fWorkspaceJson, workspaceConfig);
    } catch (IOException e) {
      throw new MojoExecutionException("could not write " + SenchaUtils.SENCHA_WORKSPACE_FILENAME, e);
    }
  }

  private Map<String, Object> getWorkspaceConfig() throws MojoExecutionException {
    return getConfig(workspaceConfigurers);
  }
}
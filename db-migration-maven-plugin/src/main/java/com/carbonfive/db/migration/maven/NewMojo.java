package com.carbonfive.db.migration.maven;

import org.apache.commons.io.*;
import org.apache.commons.lang.*;
import org.apache.commons.lang.time.*;
import org.apache.maven.plugin.*;

import java.io.*;
import java.util.*;

/**
 * @goal new
 */
public class NewMojo extends AbstractMigrationMojo
{
    /**
     * @parameter
     */
    private String versionPattern = "yyyyMMddHHmmss";

    /**
     * @parameter
     */
    private String versionTimeZone = "UTC";

    /**
     * @parameter
     */
    private String migrationExtension = ".sql";

    public void executeMojo() throws MojoExecutionException
    {
        String directory = getMigrationsPaths().get(0);

        // Remove and handle spring prefixes
        directory = StringUtils.remove(directory, "file:");

        if (directory.startsWith("classpath:"))
        {
            directory = StringUtils.remove(directory, "classpath:");
            if (directory.startsWith("/") || directory.startsWith("\""))
            {
                directory = StringUtils.substring(directory, 1);
            }
            // TODO Should pull this out of the project instead of assuming it's the default.
            directory = "src/main/resources/" + directory;
        }

        if (!(directory.startsWith("/") || directory.startsWith("\"")))
        {
            directory = project.getBasedir().getAbsolutePath() + "/" + directory;
        }

        // Remove filename wildcards
        if (directory.endsWith("*.sql") || directory.endsWith("*.*") || directory.endsWith("*.groovy") || directory.endsWith("*"))
        {
            directory = FilenameUtils.getFullPath(directory);
        }

        // Make sure the directory ends with a separator.
        if (!directory.endsWith("/") && !directory.endsWith("\""))
        {
            directory += "/";
        }

        directory = FilenameUtils.separatorsToUnix(FilenameUtils.getFullPath(directory));

        // Create the parent directories if they don't exist.
        try
        {
            new File(directory).mkdirs();
        }
        catch (Exception e)
        {
            throw new MojoExecutionException("Failed to create migrations directory: " + directory, e);
        }

        // Determine the name of the migration.
        StringBuffer sb = new StringBuffer(FastDateFormat.getInstance(getVersionPattern(), TimeZone.getTimeZone(versionTimeZone)).format(new Date()));
        String name = System.getProperty("name", "");

        if (StringUtils.isNotBlank(name))
        {
            sb.append("_").append(name);
        }
        sb.append(getMigrationExtension());

        String filename = directory + sb.toString();

        // Finally, create the file. 
        getLog().info("Creating new migration " + filename + ".");

        try
        {
            new File(filename).createNewFile();
        }
        catch (Exception e)
        {
            throw new MojoExecutionException("Failed to create migration file: " + filename, e);
        }
    }

    public String getVersionPattern()
    {
        return versionPattern;
    }

    public void setVersionTimeZone(String versionTimeZone)
    {
        this.versionTimeZone = versionTimeZone;
    }

    public String getMigrationExtension()
    {
        return migrationExtension;
    }

    @Override
    protected void validateConfiguration() throws MojoExecutionException {
        super.validateConfiguration();

        final boolean isMultipleMigrationsPathsConfigured = getMigrationsPaths().size() > 1;
        if (isMultipleMigrationsPathsConfigured)
        {
            getLog().warn("Multiple migration paths configured, using the first one: " + getMigrationsPaths().get(0));
        }
    }
}

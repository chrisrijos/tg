package ua.com.fielden.platform.test.runners;

import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import ua.com.fielden.platform.test.DbCreator;
import ua.com.fielden.platform.test.db_creators.H2DbCreator;

public class H2DomainDrivenTestCaseRunner extends AbstractDomainDrivenTestCaseRunner {

    public H2DomainDrivenTestCaseRunner(final Class<?> klass) throws Exception {
        super(klass, H2DbCreator.class);
    }

    @Override
    protected void dbCleanUp() {
        super.dbCleanUp();
        
        final Path rootPath = Paths.get(DbCreator.baseDir);
        final String mainDbFileName = databaseUri.substring(databaseUri.lastIndexOf(File.separatorChar) + 1);
        try (final Stream<Path> paths = Files.walk(rootPath)) {
            paths
                .filter(path -> path.getFileName().toString().contains(mainDbFileName))
                .map(Path::toFile)
                .peek(file -> System.out.println(format("Removing %s", file.getName())))
                .forEach(File::delete);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

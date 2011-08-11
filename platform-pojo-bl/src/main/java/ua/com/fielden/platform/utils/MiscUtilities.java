package ua.com.fielden.platform.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.filechooser.FileFilter;

import org.apache.commons.lang.StringUtils;

import ua.com.fielden.platform.basic.autocompleter.PojoValueMatcher;

public class MiscUtilities {

    /**
     * Creates file filter for files with specified extensions and filter name.
     *
     * @param extensionPattern
     * @param filterName
     * @return
     */
    public static FileFilter createFileFilter(final String filterName, final String... extensionPatterns) {
	return new FileFilter() {
	    @Override
	    public boolean accept(final File f) {
		if (f.isDirectory()) {
		    return true;
		}

		final String path = f.getAbsolutePath();
		for (final String extension : extensionPatterns) {
		    if (path.toLowerCase().endsWith(extension.toLowerCase())) {
			return true;
		    }
		}
		return false;
	    }

	    @Override
	    public String getDescription() {
		return filterName;
	    }
	};
    }

    /**
     * Creates a new array of values based on the passed list by changing * to %.
     *
     * @param criteria
     * @return
     */
    public static String[] prepare(final List<String> criteria) {
	final List<String> result = new ArrayList<String>();
	if (criteria != null) {
	    for (final String crit : criteria) {
		result.add(PojoValueMatcher.prepare(crit));
	    }
	}
	// eliminate empty or null values
	final List<String> finalRes = new ArrayList<String>();
	for (final String value : result) {
	    if (!StringUtils.isEmpty(value)) {
		finalRes.add(value);
	    }
	}
	return finalRes.toArray(new String[] {});
    }

    /**
     * Converts the content of the input stream into a string.
     *
     * @param ins
     * @return depending on the context of the input stream, may return either single or multi-line string
     * @throws IOException
     */
    public static String convertToString(final InputStream ins) throws IOException {
	final StringBuilder sb = new StringBuilder();
	String line;
	final BufferedReader reader = new BufferedReader(new InputStreamReader(ins, "UTF-8"));
	boolean needNewLine = false;
	while ((line = reader.readLine()) != null) {
	    if (needNewLine) {
		sb.append("\n");
	    } else {
		needNewLine = true;
	    }
	    sb.append(line);
	}
	return sb.toString();
    }

    public static InputStream convertToInputStream(final String value) throws UnsupportedEncodingException {
	return new ByteArrayInputStream(value.getBytes("UTF-8"));
    }

}

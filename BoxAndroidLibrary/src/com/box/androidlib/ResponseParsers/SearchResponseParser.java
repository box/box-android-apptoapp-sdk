/*******************************************************************************
 * Copyright 2011 Box.net.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.box.androidlib.ResponseParsers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.box.androidlib.Box;
import com.box.androidlib.DAO.BoxFile;
import com.box.androidlib.DAO.BoxFolder;
import com.box.androidlib.DAO.SearchResult;

/**
 * Parser for a Box API response for the search action.
 * 
 * @author developers@box.net
 */
public class SearchResponseParser extends DefaultResponseParser {

    /**
     * Search result DAO.
     */
    private final SearchResult mSearchResult = new SearchResult();
    /**
     * The BoxFolder currently being parsed.
     */
    private BoxFolder mFolder;
    /**
     * The BoxFile currently being parsed.
     */
    private BoxFile mFile;

    /**
     * Enum definition to indicate whether we are currently parsing a file or
     * folder element.
     */
    private enum FileOrFolder {
        /** Indicates that we are processing a file. */
        FILE,
        /** Indicates that we are processing a folder. */
        FOLDER
    };

    /**
     * Instance of FileOrFolder.
     */
    private FileOrFolder mFileOrFolder;

    @Override
    public void startElement(final String uri, final String localName, final String qName,
        final Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        try {
            if (localName.equals("folder")) {
                mFolder = Box.getBoxFolderClass().newInstance();
                mSearchResult.getFolders().add(mFolder);
                mFileOrFolder = FileOrFolder.FOLDER;
            } else if (localName.equals("file")) {
                mFile = Box.getBoxFileClass().newInstance();
                mSearchResult.getFiles().add(mFile);
                mFileOrFolder = FileOrFolder.FILE;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName)
        throws SAXException {
        super.endElement(uri, localName, qName);
        if (localName.equals("folder") || localName.equals("file")) {
            mFileOrFolder = null;
        } else {
            if (mFileOrFolder == FileOrFolder.FOLDER && mFolder != null) {
                mFolder.parseAttribute(localName, mTextNode.toString());
            } else if (mFileOrFolder == FileOrFolder.FILE && mFile != null) {
                mFile.parseAttribute(localName, mTextNode.toString());
            }
        }
    }

    /**
     * Get the SearchResult DAO parsed from the response.
     * 
     * @return SearchResult DAO
     */
    public SearchResult getSearchResult() {
        return mSearchResult;
    }
}

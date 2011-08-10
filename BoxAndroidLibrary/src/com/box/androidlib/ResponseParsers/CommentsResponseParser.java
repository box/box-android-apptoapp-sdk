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

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.box.androidlib.DAO.Comment;

/**
 * Parser for a response from Box API containing a list of comments.
 * 
 * @author developers@box.net
 */
public class CommentsResponseParser extends DefaultResponseParser {

    /** The comment currently being parsed. */
    private Comment mComment;
    /** The list of comments representing the response from Box API. */
    private ArrayList<Comment> mComments;

    @Override
    public void startElement(final String uri, final String localName, final String qName,
        final Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if (localName.equals("comments")) {
            mComments = new ArrayList<Comment>();
        } else if (localName.equals("comment")) {
            mComment = new Comment();
        }
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName)
        throws SAXException {
        super.endElement(uri, localName, qName);
        if (mComment != null) {
            mComment.parseAttribute(localName, mTextNode.toString());
        }
        if (localName.equals("comment")) {
            if (mComments != null && mComment != null) {
                mComments.add(mComment);
            }
        }
    }

    /**
     * Get the list of comments returned from Box API. Might be null if there
     * was an error.
     * 
     * @return list of Comments.
     */
    public ArrayList<Comment> getComments() {
        return mComments;
    }
}

package com.box.androidlib.ResponseParsers;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.box.androidlib.DAO.Collaboration;

/**
 * Response parser for get_collaborations API call.
 * 
 */
public class CollaborationsResponseParser extends DefaultResponseParser {

    /** List of collaborations to return. */
    private final List<Collaboration> mCollaborations = new ArrayList<Collaboration>();

    /** The current Collaboration item being parsed. */
    private Collaboration collaboration;

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if (localName.equals("collaboration")) {
            collaboration = new Collaboration();
        }
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if (localName.equals("collaboration")) {
            mCollaborations.add(collaboration);
            collaboration = null;
        }
        else if (collaboration != null) {
            collaboration.parseAttribute(localName, mTextNode.toString());
        }
    }

    /**
     * Get the list of Collaborations.
     * 
     * @return List of collaborations.
     */
    public List<Collaboration> getCollaborations() {
        return mCollaborations;
    }
}

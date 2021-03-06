/*
 * GNU LESSER GENERAL PUBLIC LICENSE Copyright (C) 2006 The Lobo Project
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * Contact info: xamjadmin@users.sourceforge.net
 */

package org.cobra_grendel.html.domimpl;

// import org.cobra_grendel.html.style.*;
import org.w3c.dom.html2.HTMLDivElement;

public class HTMLDivElementImpl extends HTMLAbstractUIElement implements HTMLDivElement
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public HTMLDivElementImpl(final String name, final int transactionId)
    {
        super(name, transactionId);
    }

    @Override
    public String getAlign()
    {
        return getAttribute("align");
    }

    @Override
    public void setAlign(final String align)
    {
        setAttribute("align", align);
    }

    // protected RenderState createRenderState(RenderState prevRenderState) {
    // return new BlockRenderState(prevRenderState, this);
    // }
}

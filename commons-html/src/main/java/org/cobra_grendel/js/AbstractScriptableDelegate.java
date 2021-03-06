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
package org.cobra_grendel.js;

import java.io.Serializable;

import org.mozilla.javascript.Scriptable;

public abstract class AbstractScriptableDelegate implements ScriptableDelegate, Serializable
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private Scriptable scriptable;

    protected final int transactionId;

    public AbstractScriptableDelegate(final int transactionId)
    {
        super();
        this.transactionId = transactionId;
    }

    @Override
    public Scriptable getScriptable()
    {
        return scriptable;
    }

    @Override
    public void setScriptable(final Scriptable scriptable)
    {
        this.scriptable = scriptable;
    }
}

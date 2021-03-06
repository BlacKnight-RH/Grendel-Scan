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
package org.cobra_grendel.html.js;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.security.SecureClassLoader;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.GeneratedClassLoader;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.SecurityController;

public class SecurityControllerImpl extends SecurityController
{
    private class LocalSecureClassLoader extends SecureClassLoader implements GeneratedClassLoader
    {
        public LocalSecureClassLoader(final ClassLoader parent)
        {
            super(parent);
        }

        @Override
        public Class defineClass(final String name, final byte[] b)
        {
            return this.defineClass(name, b, 0, b.length, codesource);
        }

        @Override
        public void linkClass(final Class clazz)
        {
            super.resolveClass(clazz);
        }
    }

    private final CodeSource codesource;
    private final java.security.Policy policy;

    private final java.net.URL url;

    public SecurityControllerImpl(final java.net.URL url, final Policy policy)
    {
        this.url = url;
        this.policy = policy;
        codesource = new CodeSource(this.url, (java.security.cert.Certificate[]) null);
    }

    @Override
    public Object callWithDomain(final Object securityDomain, final Context ctx, final Callable callable, final Scriptable scope, final Scriptable thisObj, final Object[] args)
    {
        if (securityDomain == null)
        {
            return callable.call(ctx, scope, thisObj, args);
        }
        else
        {
            PrivilegedAction action = new PrivilegedAction()
            {
                @Override
                public Object run()
                {
                    return callable.call(ctx, scope, thisObj, args);
                }
            };
            AccessControlContext acctx = new AccessControlContext(new ProtectionDomain[] { (ProtectionDomain) securityDomain });
            return AccessController.doPrivileged(action, acctx);
        }
    }

    @Override
    public GeneratedClassLoader createClassLoader(final ClassLoader parent, final Object staticDomain)
    {
        return new LocalSecureClassLoader(parent);
    }

    @Override
    public Object getDynamicSecurityDomain(final Object securityDomain)
    {
        Policy policy = this.policy;
        if (policy == null)
        {
            return null;
        }
        else
        {
            PermissionCollection permissions = this.policy.getPermissions(codesource);
            return new ProtectionDomain(codesource, permissions);
        }
    }
}

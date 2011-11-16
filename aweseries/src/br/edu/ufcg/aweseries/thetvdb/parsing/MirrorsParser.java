/*
 *   MirrorsParser.java
 *
 *   Copyright 2011 Cleber Gonçalves de Sousa, Gabriel Assis Bezerra
 *                  and Tiago Almeida Reul
 *
 *   All rights reserved.
 *
 *   This file is part of aweseries.
 *
 *   aweseries is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   aweseries is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with aweseries.  If not, see <http://www.gnu.org/licenses/>.
 *
 *   Contributors:
 *      Cleber Gonçalves de Sousa
 *      Gabriel Assis Bezerra
 *      Tiago Almeida Reul
 */

//  <?xml version="1.0" encoding="UTF-8" ?>
//  <Mirrors>
//   <Mirror>
//     <id>1</id>
//     <mirrorpath>http://thetvdb.com</mirrorpath>
//     <typemask>7</typemask>
//   </Mirror>
//  </Mirrors>

package br.edu.ufcg.aweseries.thetvdb.parsing;

import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.SAXException;

import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Xml;
import br.edu.ufcg.aweseries.thetvdb.stream.url.MirrorBuilder;
import br.edu.ufcg.aweseries.thetvdb.stream.url.Mirrors;

public class MirrorsParser {

    private InputStream inputStream;

    public MirrorsParser(InputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalArgumentException("inputStream should not be null");
        }

        this.inputStream = inputStream;
    }

    public Mirrors parse() {
        final Mirrors mirrors = new Mirrors();
        final MirrorBuilder builder = new MirrorBuilder();

        final RootElement root = new RootElement("Mirrors");
        final Element element = root.getChild("Mirror");

        element.setEndElementListener(
                new EndElementListener() {
                    @Override
                    public void end() {
                        mirrors.add(builder.build());
                    }
                }
        );

        element.getChild("mirrorpath").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        builder.withPath(body);
                    }
                }
        );

        element.getChild("typemask").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        builder.withTypeMask(body);
                    }
                }
        );

        try {
            Xml.parse(this.inputStream, Xml.Encoding.UTF_8, root.getContentHandler());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }

        return mirrors;
    }
}
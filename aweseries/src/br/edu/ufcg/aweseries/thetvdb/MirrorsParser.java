//	<?xml version="1.0" encoding="UTF-8" ?>
//	<Mirrors>
//	 <Mirror>
//	   <id>1</id>
//	   <mirrorpath>http://thetvdb.com</mirrorpath>
//	   <typemask>7</typemask>
//	 </Mirror>
//	</Mirrors>

package br.edu.ufcg.aweseries.thetvdb;

import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Xml;

public class MirrorsParser extends TheTVDBParser<Mirrors> {

    public MirrorsParser(String url) {
	    super(url);
	}

    @Override
    public Mirrors parse() {
        final Mirrors mirrors = new Mirrors();
        final Mirror mirror = new Mirror();

        RootElement root = new RootElement("Mirrors");
        Element element = root.getChild("Mirror");
        element.setEndElementListener(
                new EndElementListener() {
                    public void end() {
                        mirrors.add(mirror.copy());
                    }
                }
        );
        element.getChild("mirrorpath").setEndTextElementListener(
                new EndTextElementListener() {
                    public void end(String body) {
                        mirror.setPath(body);
                    }
                }
        );
        element.getChild("typemask").setEndTextElementListener(
                new EndTextElementListener() {
                    public void end(String body) {
                        mirror.setTypeMask(Integer.valueOf(body));
                    }
                }
        );

        try {
            Xml.parse(this.getInputStream(), Xml.Encoding.UTF_8, 
                    root.getContentHandler());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return mirrors;
    }
}
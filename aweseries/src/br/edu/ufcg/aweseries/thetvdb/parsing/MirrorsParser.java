//  <?xml version="1.0" encoding="UTF-8" ?>
//  <Mirrors>
//   <Mirror>
//     <id>1</id>
//     <mirrorpath>http://thetvdb.com</mirrorpath>
//     <typemask>7</typemask>
//   </Mirror>
//  </Mirrors>

package br.edu.ufcg.aweseries.thetvdb.parsing;

import java.io.InputStream;

import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Xml;
import br.edu.ufcg.aweseries.thetvdb.stream.url.MirrorBuilder;
import br.edu.ufcg.aweseries.thetvdb.stream.url.Mirrors;

public class MirrorsParser extends TheTVDBParser<Mirrors> {

    public MirrorsParser(InputStream inputStream) {
        super(inputStream);
    }

    @Override
    public Mirrors parse() {
        final Mirrors mirrors = new Mirrors();
        final MirrorBuilder builder = new MirrorBuilder();

        final RootElement root = new RootElement("Mirrors");
        final Element element = root.getChild("Mirror");

        element.setEndElementListener(
                new EndElementListener() {
                    public void end() {
                        mirrors.add(builder.build());
                    }
                }
        );

        element.getChild("mirrorpath").setEndTextElementListener(
                new EndTextElementListener() {
                    public void end(String body) {
                        builder.withPath(body);
                    }
                }
        );

        element.getChild("typemask").setEndTextElementListener(
                new EndTextElementListener() {
                    public void end(String body) {
                    	builder.withTypeMask(body);
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
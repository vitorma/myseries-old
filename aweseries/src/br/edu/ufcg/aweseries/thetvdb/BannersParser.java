//<?xml version="1.0" encoding="UTF-8" ?>
//<Banners>
//   <Banner>
//      <id>14820</id>
//      <BannerPath>text/80348.jpg</BannerPath>
//      <BannerType>series</BannerType>
//      <BannerType2>text</BannerType2>
//      <Language>en</Language>
//      <Season></Season>
//   </Banner>
//   <Banner>
//      <id>14821</id>
//      <BannerPath>blank/80348.jpg</BannerPath>
//      <BannerType>series</BannerType>
//      <BannerType2>blank</BannerType2>
//      <Language></Language>
//      <Season></Season>
//   </Banner>
//   <Banner>
//      <id>14827</id>
//      <BannerPath>graphical/80348-g.jpg</BannerPath>
//      <BannerType>series</BannerType>
//      <BannerType2>graphical</BannerType2>
//      <Language>en</Language>
//      <Season></Season>
//   </Banner>
//   <Banner>
//      <id>15217</id>
//      <BannerPath>seasons/80348-1.jpg</BannerPath>
//      <BannerType>season</BannerType>
//      <BannerType2>season</BannerType2>
//      <Language>en</Language>
//      <Season>1</Season>
//   </Banner>
//</Banners>

package br.edu.ufcg.aweseries.thetvdb;

import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Xml;

public class BannersParser extends TheTVDBParser<Banners> {

	public BannersParser(String url) {
		super(url);
	}

	@Override
	public Banners parse() {
		final Banners banners = new Banners();
		final Banner banner = new Banner();

		RootElement root = new RootElement("Banners");
        Element element = root.getChild("Banner");
        element.setEndElementListener(
                new EndElementListener() {
                    public void end() {
                        banners.add(banner.copy());
                    }
                }
        );

        element.getChild("id").setEndTextElementListener(
                new EndTextElementListener() {
                    public void end(String body) {
                        banner.setId(body);
                    }
                }
        );

        element.getChild("BannerPath").setEndTextElementListener(
                new EndTextElementListener() {
                    public void end(String body) {
                        banner.setPath(body);
                    }
                }
        );

        element.getChild("BannerType").setEndTextElementListener(
                new EndTextElementListener() {
                    public void end(String body) {
                        banner.setType(body);
                    }
                }
        );

        element.getChild("BannerType2").setEndTextElementListener(
                new EndTextElementListener() {
                    public void end(String body) {
                        banner.setType2(body);
                    }
                }
        );

        element.getChild("Season").setEndTextElementListener(
                new EndTextElementListener() {
                    public void end(String body) {
                        banner.setSeason(body);
                    }
                }
        );

        try {
            Xml.parse(this.getInputStream(), Xml.Encoding.UTF_8, 
                    root.getContentHandler());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return banners;
	}	
}

package br.edu.ufcg.aweseries.thetvdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Banners {
	private Map<BannerType, Map<BannerType2, List<Banner>>> banners;

	public Banners() {
		this.banners = new HashMap<BannerType, Map<BannerType2,List<Banner>>>();
		for (BannerType bt : BannerType.values()) {
			Map<BannerType2, List<Banner>> b = new HashMap<BannerType2, List<Banner>>();
			for (BannerType2 bt2 : bt.getBannerType2()) {
				b.put(bt2, new ArrayList<Banner>());
				this.banners.put(bt, b);
			}
		}
	}

	public void add(Banner b) {
		if (b == null) {return;}
		for (BannerType bt : BannerType.values()) {
			if (bt.name().equalsIgnoreCase(b.getType())) {
				for (BannerType2 bt2 : bt.getBannerType2()) {
					if (bt2.name().equalsIgnoreCase(b.getType2())) {
						this.banners.get(bt).get(bt2).add(b);
					}
				}
			}
		}
	}

	private Banner getBanner(BannerType bt, BannerType2 bt2) {
		if (this.banners.get(bt).get(bt2).isEmpty()) {return null;}
		return this.banners.get(bt).get(bt2).get(0);
	}

	public Banner getSeriesBanner() {
		Banner b = this.getBanner(BannerType.SERIES, BannerType2.GRAPHICAL);
		if (b == null) {
			b = this.getBanner(BannerType.SERIES, BannerType2.TEXT);
		}
		if (b == null) {
			b = this.getBanner(BannerType.SERIES, BannerType2.BLANK);
		}
		return b;
	}
}

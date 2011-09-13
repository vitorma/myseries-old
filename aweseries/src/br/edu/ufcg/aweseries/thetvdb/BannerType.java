package br.edu.ufcg.aweseries.thetvdb;

public enum BannerType {
	SERIES {
		@Override
		public BannerType2[] getBannerType2() {
			return new BannerType2[] {
					BannerType2.BLANK, BannerType2.TEXT, BannerType2.GRAPHICAL};
		}
	},

	SEASON {
		@Override
		public BannerType2[] getBannerType2() {
			return new BannerType2[] {
					BannerType2.SEASON, BannerType2.SEASONWIDE};
		}
	};

	public abstract BannerType2[] getBannerType2();
}

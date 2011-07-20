package frontlinesms2

enum ConnectionStatus {
	NOT_CONNECTED("red"),
	CONNECTED("green"),
	ERROR("orange")

	String indicator;

	public ConnectionStatus(String color) {
		this.indicator = color;
	}
}
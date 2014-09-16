package com.oakonell.triplica.model;

public class Triplica {
	public int startStackNum;
	public Position startPosition;
	public int endStackNum;
	public Position endPosition;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((endPosition == null) ? 0 : endPosition.hashCode());
		result = prime * result + endStackNum;
		result = prime * result
				+ ((startPosition == null) ? 0 : startPosition.hashCode());
		result = prime * result + startStackNum;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Triplica other = (Triplica) obj;
		if (endPosition != other.endPosition)
			return false;
		if (endStackNum != other.endStackNum)
			return false;
		if (startPosition != other.startPosition)
			return false;
		if (startStackNum != other.startStackNum)
			return false;
		return true;
	}

	public String toString() {
		return "Triplica: " + startStackNum + ", " + startPosition + " => "
				+ endStackNum + ", " + endPosition;
	}
}
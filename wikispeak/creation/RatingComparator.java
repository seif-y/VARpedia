package wikispeak.creation;

import java.util.Comparator;

public class RatingComparator implements Comparator<Creation> {

	@Override
	public int compare(Creation cr1, Creation cr2) {
		
		if (cr1.getRating() > cr2.getRating()) {
			return 1;
		} else if (cr1.getRating() < cr2.getRating()) {
			return -1;
		}
		
		return 0;
	}

}

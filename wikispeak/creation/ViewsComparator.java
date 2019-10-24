package wikispeak.creation;

import java.util.Comparator;

public class ViewsComparator implements Comparator<Creation> {

	@Override
	public int compare(Creation cr1, Creation cr2) {
		
		if (cr1.getViews() > cr2.getViews()) {
			return 1;
		} else if (cr1.getViews() < cr2.getViews()) {
			return -1;
		}
		
		return 0;
	}

}

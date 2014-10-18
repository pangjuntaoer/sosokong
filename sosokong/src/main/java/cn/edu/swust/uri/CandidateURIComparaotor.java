package cn.edu.swust.uri;

import java.util.Comparator;

public class CandidateURIComparaotor implements Comparator<CandidateURI>{

	public int compare(CandidateURI o1, CandidateURI o2) {
		if(o1.getCandidateURI().indexOf("search")>0){
			return 1;
		}
		return 0;
	}

}

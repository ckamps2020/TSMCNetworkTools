package com.thesquadmc.networktools;

import com.thesquadmc.networktools.abstraction.AbstractionModule;
import com.thesquadmc.networktools.abstraction.NMSAbstract;
import com.thesquadmc.networktools.abstraction.v1_12_R1.NMSAbstract1_12_R1;

@AbstractionModule(version = "1.12-R1")
public class Main1_12_R1 {
	
	public NMSAbstract getImplementation() {
		return new NMSAbstract1_12_R1();
	}
	
}
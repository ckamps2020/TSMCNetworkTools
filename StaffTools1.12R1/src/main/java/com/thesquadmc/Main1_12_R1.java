package com.thesquadmc;

import com.thesquadmc.abstraction.AbstractionModule;
import com.thesquadmc.abstraction.NMSAbstract;
import com.thesquadmc.abstraction.v1_12_R1.NMSAbstract1_12_R1;

@AbstractionModule(version = "1.12-R1")
public class Main1_12_R1 {
	
	public NMSAbstract getImplementation() {
		return new NMSAbstract1_12_R1();
	}
	
}
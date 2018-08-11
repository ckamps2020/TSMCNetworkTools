package com.thesquadmc.networktools;

import com.thesquadmc.networktools.abstraction.AbstractionModule;
import com.thesquadmc.networktools.abstraction.NMSAbstract;
import com.thesquadmc.networktools.abstraction.v1_8_R3.NMSAbstract1_8_R3;

@AbstractionModule(version = "1.8-R3")
public class Main1_8_R3 {

    public NMSAbstract getImplementation() {
        return new NMSAbstract1_8_R3();
    }

}
package ca.sfu.cmpt362.ayusharora.myruns.weka.classifiers;

// Copied the entire source code given by weka classifier

import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.RevisionUtils;
import weka.classifiers.AbstractClassifier;

public class WekaWrapper
        extends AbstractClassifier {

    /**
     * Returns only the toString() method.
     *
     * @return a string describing the classifier
     */
    public String globalInfo() {
        return toString();
    }

    /**
     * Returns the capabilities of this classifier.
     *
     * @return the capabilities
     */
    public Capabilities getCapabilities() {
        weka.core.Capabilities result = new weka.core.Capabilities(this);

        result.enable(weka.core.Capabilities.Capability.NOMINAL_ATTRIBUTES);
        result.enable(weka.core.Capabilities.Capability.NUMERIC_ATTRIBUTES);
        result.enable(weka.core.Capabilities.Capability.DATE_ATTRIBUTES);
        result.enable(weka.core.Capabilities.Capability.MISSING_VALUES);
        result.enable(weka.core.Capabilities.Capability.NOMINAL_CLASS);
        result.enable(weka.core.Capabilities.Capability.MISSING_CLASS_VALUES);


        result.setMinimumNumberInstances(0);

        return result;
    }

    /**
     * only checks the data against its capabilities.
     *
     * @param i the training data
     */
    public void buildClassifier(Instances i) throws Exception {
        // can classifier handle the data?
        getCapabilities().testWithFail(i);
    }

    /**
     * Classifies the given instance.
     *
     * @param i the instance to classify
     * @return the classification result
     */
    public double classifyInstance(Instance i) throws Exception {
        Object[] s = new Object[i.numAttributes()];

        for (int j = 0; j < s.length; j++) {
            if (!i.isMissing(j)) {
                if (i.attribute(j).isNominal())
                    s[j] = new String(i.stringValue(j));
                else if (i.attribute(j).isNumeric())
                    s[j] = new Double(i.value(j));
            }
        }

        // set class value to missing
        s[i.classIndex()] = null;

        return WekaClassifier.classify(s);
    }

    /**
     * Returns the revision string.
     *
     * @return        the revision
     */
    public String getRevision() {
        return RevisionUtils.extract("1.0");
    }

    /**
     * Returns only the classnames and what classifier it is based on.
     *
     * @return a short description
     */
    public String toString() {
        return "Auto-generated classifier wrapper, based on weka.classifiers.trees.J48 (generated with Weka 3.8.6).\n" + this.getClass().getName() + "/WekaClassifier";
    }

    /**
     * Runs the classfier from commandline.
     *
     * @param args the commandline arguments
     */
    public static void main(String args[]) {
        runClassifier(new WekaWrapper(), args);
    }
}

class WekaClassifier {

    public static double classify(Object[] i)
            throws Exception {

        double p = Double.NaN;
        p = WekaClassifier.Ncbcf96a0(i);
        return p;
    }
    static double Ncbcf96a0(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 1;
        } else if (((Double) i[0]).doubleValue() <= 317.480652) {
            p = WekaClassifier.N3e85359f1(i);
        } else if (((Double) i[0]).doubleValue() > 317.480652) {
            p = WekaClassifier.Nd9a23b52(i);
        }
        return p;
    }
    static double N3e85359f1(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 55.463738) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() > 55.463738) {
            p = 1;
        }
        return p;
    }
    static double Nd9a23b52(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() <= 1169.148692) {
            p = WekaClassifier.N558119043(i);
        } else if (((Double) i[0]).doubleValue() > 1169.148692) {
            p = WekaClassifier.N30b9324d14(i);
        }
        return p;
    }
    static double N558119043(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 2;
        } else if (((Double) i[4]).doubleValue() <= 18.633608) {
            p = WekaClassifier.N3e9dcd774(i);
        } else if (((Double) i[4]).doubleValue() > 18.633608) {
            p = WekaClassifier.N757837ec6(i);
        }
        return p;
    }
    static double N3e9dcd774(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() <= 1103.328589) {
            p = WekaClassifier.N29232ef65(i);
        } else if (((Double) i[0]).doubleValue() > 1103.328589) {
            p = 3;
        }
        return p;
    }
    static double N29232ef65(Object []i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 3;
        } else if (((Double) i[7]).doubleValue() <= 1.076632) {
            p = 3;
        } else if (((Double) i[7]).doubleValue() > 1.076632) {
            p = 2;
        }
        return p;
    }
    static double N757837ec6(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() <= 242.395276) {
            p = WekaClassifier.N286c497e7(i);
        } else if (((Double) i[1]).doubleValue() > 242.395276) {
            p = WekaClassifier.N36b002e211(i);
        }
        return p;
    }
    static double N286c497e7(Object []i) {
        double p = Double.NaN;
        if (i[18] == null) {
            p = 2;
        } else if (((Double) i[18]).doubleValue() <= 7.954696) {
            p = 2;
        } else if (((Double) i[18]).doubleValue() > 7.954696) {
            p = WekaClassifier.N784acf718(i);
        }
        return p;
    }
    static double N784acf718(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 2;
        } else if (((Double) i[5]).doubleValue() <= 48.759284) {
            p = WekaClassifier.N48ae1a109(i);
        } else if (((Double) i[5]).doubleValue() > 48.759284) {
            p = 2;
        }
        return p;
    }
    static double N48ae1a109(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 2;
        } else if (((Double) i[5]).doubleValue() <= 48.177777) {
            p = WekaClassifier.N7cdd83be10(i);
        } else if (((Double) i[5]).doubleValue() > 48.177777) {
            p = 3;
        }
        return p;
    }
    static double N7cdd83be10(Object []i) {
        double p = Double.NaN;
        if (i[11] == null) {
            p = 3;
        } else if (((Double) i[11]).doubleValue() <= 11.70486) {
            p = 3;
        } else if (((Double) i[11]).doubleValue() > 11.70486) {
            p = 2;
        }
        return p;
    }
    static double N36b002e211(Object []i) {
        double p = Double.NaN;
        if (i[27] == null) {
            p = 2;
        } else if (((Double) i[27]).doubleValue() <= 6.421038) {
            p = WekaClassifier.N182f620212(i);
        } else if (((Double) i[27]).doubleValue() > 6.421038) {
            p = 3;
        }
        return p;
    }
    static double N182f620212(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 3;
        } else if (((Double) i[2]).doubleValue() <= 129.330538) {
            p = WekaClassifier.N2069858d13(i);
        } else if (((Double) i[2]).doubleValue() > 129.330538) {
            p = 2;
        }
        return p;
    }
    static double N2069858d13(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 3;
        } else if (((Double) i[4]).doubleValue() <= 50.348277) {
            p = 3;
        } else if (((Double) i[4]).doubleValue() > 50.348277) {
            p = 2;
        }
        return p;
    }
    static double N30b9324d14(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 3;
        } else if (((Double) i[0]).doubleValue() <= 1325.776775) {
            p = WekaClassifier.N62881aa15(i);
        } else if (((Double) i[0]).doubleValue() > 1325.776775) {
            p = 3;
        }
        return p;
    }
    static double N62881aa15(Object []i) {
        double p = Double.NaN;
        if (i[64] == null) {
            p = 3;
        } else if (((Double) i[64]).doubleValue() <= 29.679727) {
            p = 3;
        } else if (((Double) i[64]).doubleValue() > 29.679727) {
            p = WekaClassifier.N6574cc1f16(i);
        }
        return p;
    }
    static double N6574cc1f16(Object []i) {
        double p = Double.NaN;
        if (i[26] == null) {
            p = 2;
        } else if (((Double) i[26]).doubleValue() <= 7.504806) {
            p = WekaClassifier.N95d68d117(i);
        } else if (((Double) i[26]).doubleValue() > 7.504806) {
            p = 3;
        }
        return p;
    }
    static double N95d68d117(Object []i) {
        double p = Double.NaN;
        if (i[14] == null) {
            p = 3;
        } else if (((Double) i[14]).doubleValue() <= 1.364718) {
            p = 3;
        } else if (((Double) i[14]).doubleValue() > 1.364718) {
            p = WekaClassifier.N708f18ed18(i);
        }
        return p;
    }
    static double N708f18ed18(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 2;
        } else if (((Double) i[4]).doubleValue() <= 48.864007) {
            p = WekaClassifier.N32e8f28719(i);
        } else if (((Double) i[4]).doubleValue() > 48.864007) {
            p = 2;
        }
        return p;
    }
    static double N32e8f28719(Object []i) {
        double p = Double.NaN;
        if (i[16] == null) {
            p = 2;
        } else if (((Double) i[16]).doubleValue() <= 5.912258) {
            p = WekaClassifier.N713fc4f120(i);
        } else if (((Double) i[16]).doubleValue() > 5.912258) {
            p = 3;
        }
        return p;
    }
    static double N713fc4f120(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() <= 272.809392) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() > 272.809392) {
            p = WekaClassifier.N13c631e721(i);
        }
        return p;
    }
    static double N13c631e721(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 3;
        } else if (((Double) i[0]).doubleValue() <= 1244.324114) {
            p = 3;
        } else if (((Double) i[0]).doubleValue() > 1244.324114) {
            p = 2;
        }
        return p;
    }
}

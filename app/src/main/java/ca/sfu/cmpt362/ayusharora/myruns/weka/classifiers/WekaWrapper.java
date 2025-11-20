package ca.sfu.cmpt362.ayusharora.myruns.weka.classifiers;

import androidx.annotation.NonNull;

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
                    s[j] = i.stringValue(j);
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
    @NonNull
    public String toString() {
        return "Auto-generated classifier wrapper, based on weka.classifiers.trees.J48 (generated with Weka 3.8.6).\n" + this.getClass().getName() + "/WekaClassifier";
    }

    /**
     * Runs the classfier from commandline.
     *
     * @param args the commandline arguments
     */
    public static void main(String[] args) {
        runClassifier(new WekaWrapper(), args);
    }
}

class WekaClassifier {

    public static double classify(Object[] i)
            throws Exception {

        double p = Double.NaN;
        p = WekaClassifier.N4b1364630(i);
        return p;
    }
    static double N4b1364630(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 1;
        } else if ((Double) i[0] <= 317.480652) {
            p = WekaClassifier.N196fd1c31(i);
        } else if ((Double) i[0] > 317.480652) {
            p = WekaClassifier.N45e27fe32(i);
        }
        return p;
    }
    static double N196fd1c31(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if ((Double) i[0] <= 55.463738) {
            p = 0;
        } else if ((Double) i[0] > 55.463738) {
            p = 1;
        }
        return p;
    }
    static double N45e27fe32(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 2;
        } else if ((Double) i[0] <= 1169.148692) {
            p = WekaClassifier.N246de3823(i);
        } else if ((Double) i[0] > 1169.148692) {
            p = WekaClassifier.N6df5847814(i);
        }
        return p;
    }
    static double N246de3823(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 2;
        } else if ((Double) i[4] <= 18.633608) {
            p = WekaClassifier.N740dbb104(i);
        } else if ((Double) i[4] > 18.633608) {
            p = WekaClassifier.N45c0e56e6(i);
        }
        return p;
    }
    static double N740dbb104(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 2;
        } else if ((Double) i[0] <= 1103.328589) {
            p = WekaClassifier.N138b8d9b5(i);
        } else if ((Double) i[0] > 1103.328589) {
            p = 3;
        }
        return p;
    }
    static double N138b8d9b5(Object []i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 3;
        } else if ((Double) i[7] <= 1.076632) {
            p = 3;
        } else if ((Double) i[7] > 1.076632) {
            p = 2;
        }
        return p;
    }
    static double N45c0e56e6(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 2;
        } else if ((Double) i[1] <= 242.395276) {
            p = WekaClassifier.Ne7a7fa77(i);
        } else if ((Double) i[1] > 242.395276) {
            p = WekaClassifier.N476d963511(i);
        }
        return p;
    }
    static double Ne7a7fa77(Object []i) {
        double p = Double.NaN;
        if (i[18] == null) {
            p = 2;
        } else if ((Double) i[18] <= 7.954696) {
            p = 2;
        } else if ((Double) i[18] > 7.954696) {
            p = WekaClassifier.N173690438(i);
        }
        return p;
    }
    static double N173690438(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 2;
        } else if ((Double) i[5] <= 48.759284) {
            p = WekaClassifier.N581e306b9(i);
        } else if ((Double) i[5] > 48.759284) {
            p = 2;
        }
        return p;
    }
    static double N581e306b9(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 2;
        } else if ((Double) i[5] <= 48.177777) {
            p = WekaClassifier.N1a37459310(i);
        } else if ((Double) i[5] > 48.177777) {
            p = 3;
        }
        return p;
    }
    static double N1a37459310(Object []i) {
        double p = Double.NaN;
        if (i[11] == null) {
            p = 3;
        } else if ((Double) i[11] <= 11.70486) {
            p = 3;
        } else if ((Double) i[11] > 11.70486) {
            p = 2;
        }
        return p;
    }
    static double N476d963511(Object []i) {
        double p = Double.NaN;
        if (i[27] == null) {
            p = 2;
        } else if ((Double) i[27] <= 6.421038) {
            p = WekaClassifier.N64faa21312(i);
        } else if ((Double) i[27] > 6.421038) {
            p = 3;
        }
        return p;
    }
    static double N64faa21312(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 3;
        } else if ((Double) i[2] <= 129.330538) {
            p = WekaClassifier.N4dce5b8f13(i);
        } else if ((Double) i[2] > 129.330538) {
            p = 2;
        }
        return p;
    }
    static double N4dce5b8f13(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 3;
        } else if ((Double) i[4] <= 50.348277) {
            p = 3;
        } else if ((Double) i[4] > 50.348277) {
            p = 2;
        }
        return p;
    }
    static double N6df5847814(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 3;
        } else if ((Double) i[0] <= 1325.776775) {
            p = WekaClassifier.N46027bcd15(i);
        } else if ((Double) i[0] > 1325.776775) {
            p = 3;
        }
        return p;
    }
    static double N46027bcd15(Object []i) {
        double p = Double.NaN;
        if (i[64] == null) {
            p = 3;
        } else if ((Double) i[64] <= 29.679727) {
            p = 3;
        } else if ((Double) i[64] > 29.679727) {
            p = WekaClassifier.N7759b32416(i);
        }
        return p;
    }
    static double N7759b32416(Object []i) {
        double p = Double.NaN;
        if (i[26] == null) {
            p = 2;
        } else if ((Double) i[26] <= 7.504806) {
            p = WekaClassifier.N48f48c6317(i);
        } else if ((Double) i[26] > 7.504806) {
            p = 3;
        }
        return p;
    }
    static double N48f48c6317(Object []i) {
        double p = Double.NaN;
        if (i[14] == null) {
            p = 3;
        } else if ((Double) i[14] <= 1.364718) {
            p = 3;
        } else if ((Double) i[14] > 1.364718) {
            p = WekaClassifier.N963074118(i);
        }
        return p;
    }
    static double N963074118(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 2;
        } else if ((Double) i[4] <= 48.864007) {
            p = WekaClassifier.N5c6ad9b319(i);
        } else if ((Double) i[4] > 48.864007) {
            p = 2;
        }
        return p;
    }
    static double N5c6ad9b319(Object []i) {
        double p = Double.NaN;
        if (i[16] == null) {
            p = 2;
        } else if ((Double) i[16] <= 5.912258) {
            p = WekaClassifier.N6610818920(i);
        } else if ((Double) i[16] > 5.912258) {
            p = 3;
        }
        return p;
    }
    static double N6610818920(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 2;
        } else if ((Double) i[1] <= 272.809392) {
            p = 2;
        } else if ((Double) i[1] > 272.809392) {
            p = WekaClassifier.Naeb9ebc21(i);
        }
        return p;
    }
    static double Naeb9ebc21(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 3;
        } else if ((Double) i[0] <= 1244.324114) {
            p = 3;
        } else if ((Double) i[0] > 1244.324114) {
            p = 2;
        }
        return p;
    }
}

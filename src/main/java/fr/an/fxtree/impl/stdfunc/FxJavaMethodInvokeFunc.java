package fr.an.fxtree.impl.stdfunc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.WeakHashMap;

import fr.an.fxtree.impl.helper.FxNodeValueUtils;
import fr.an.fxtree.model.FxArrayNode;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.func.FxEvalContext;
import fr.an.fxtree.model.func.FxNodeFunc;

public class FxJavaMethodInvokeFunc extends FxNodeFunc {

    public static final String NAME = "invoke";

    private static final WeakHashMap<String,Method> cacheMethodByFQN = new WeakHashMap<>();

    // ------------------------------------------------------------------------

    public static final FxJavaMethodInvokeFunc INSTANCE = new FxJavaMethodInvokeFunc();

    private FxJavaMethodInvokeFunc() {
    }

    // ------------------------------------------------------------------------

    @Override
    public void eval(FxChildWriter dest, FxEvalContext ctx, FxNode src) {
        FxObjNode srcObj = (FxObjNode) src;
        String fqn =  FxNodeValueUtils.getStringOrThrow(srcObj, "method");

        final Method method = resolvePublicStaticMethodByFQN(fqn);
        final Parameter[] methodParameters = method.getParameters();
        final int methodParametersCount = methodParameters.length;

        Object[] paramValues = new Object[methodParametersCount];
        if (methodParametersCount == 0) {
            paramValues = new Object[0];
        } else {
            FxNode paramsNode = srcObj.get("params");
            int paramValuesCount = 0;
            if (paramsNode != null) {
                if (paramsNode instanceof FxArrayNode) {
                    FxArrayNode arr = (FxArrayNode) paramsNode;
                    paramValuesCount = arr.size();
                    if (paramValuesCount > methodParametersCount) {
                        // TODO .. ellipsis params not supported yet: "f(A a, B b, C... c)"
                        throw new IllegalArgumentException("too many params");
                    }
                    int lastParam = Math.min(paramValuesCount, methodParametersCount);
                    for(int i = 0; i < lastParam; i++) {
                        FxNode paramNode = arr.get(i);
                        Class<?> type = methodParameters[i].getType();
                        paramValues[i] = FxNodeValueUtils.nodeToValueForType(paramNode, type, ctx);
                    }
                } else {
                    throw new IllegalArgumentException("expecting 'params' as array[" + methodParametersCount + "], got " + paramsNode.getNodeType());
                }
            }
            if (paramValuesCount < methodParametersCount) {
                FxObjNode namedParamsNode = (FxObjNode) srcObj.get("namedParams");
                if (namedParamsNode != null) {
                    for(Iterator<Entry<String, FxNode>> paramFieldIter = namedParamsNode.fields(); paramFieldIter.hasNext(); ) {
                        Entry<String, FxNode> e = paramFieldIter.next();
                        String paramName = e.getKey();
                        FxNode paramValue = e.getValue();
                        // lookup method parameter for name
                        int index = findMethodParameterByName(methodParameters, paramName);
                        if (index < 0) {
                            throw new IllegalArgumentException("named parameter '" + paramName + "' not found on method");
                        }
                        Class<?> type = methodParameters[index].getType();
                        paramValues[index] = FxNodeValueUtils.nodeToValueForType(paramValue, type, ctx);
                        paramValuesCount++;
                    }
                }
                if (paramValuesCount < methodParametersCount) {
                    for (int i = 0; i < methodParameters.length; i++) {
                        Parameter p = methodParameters[i];
                        if (paramValues[i] == null) {
                            if (FxChildWriter.class.isAssignableFrom(p.getType())) {
                                paramValues[i] = dest;
                                paramValuesCount++;
                            } else if (FxNode.class.isAssignableFrom(p.getType())) {
                                paramValues[i] = src;
                                paramValuesCount++;
                            } else if (FxEvalContext.class.isAssignableFrom(p.getType())) {
                                paramValues[i] = ctx;
                                paramValuesCount++;
                            }
                        }
                    }
                }
                if (paramValuesCount < methodParametersCount) {
                    throw new IllegalArgumentException("missing method params");
                }
            }
        }

        Object tmpres;
        try {
            // *** do invoke ***
            tmpres = method.invoke(null, paramValues);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new RuntimeException("Failed to invoke method " + fqn, ex);
        }

        if (tmpres != null) {
            // TOADD .. may write output?
        }
    }

    // ------------------------------------------------------------------------

    protected Method resolvePublicStaticMethodByFQN(String fqn) {
        Method res = cacheMethodByFQN.get(fqn);
        if (res != null) {
            return res;
        }
        int indexDot = fqn.lastIndexOf('.');
        if (indexDot == -1) {
            throw new IllegalArgumentException("param 'method' expected to be <<fullyQualified className>>.<<methodName>>");
        }
        String className = fqn.substring(0, indexDot);
        String methodName = fqn.substring(indexDot + 1, fqn.length());

        Class<?> clss;
        try {
            clss = Class.forName(className);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("Failed to invoke javaMethod: can not load Class.forName '" + className + "'", ex);
        }

        Method[] methods = clss.getMethods();
        for(Method m : methods) {
            if (! m.getName().equals(methodName)) {
                continue;
            }
            if (0 == (m.getModifiers() & Modifier.PUBLIC)) {
                continue;
            }
            if (0 == (m.getModifiers() & Modifier.STATIC)) {
                continue;
            }
            res = m;
            break;
        }
        if (res == null) {
            throw new RuntimeException("Failed to invoke javaMethod: public static method '" + methodName + "' not found in class '" + className);
        }
        cacheMethodByFQN.put(fqn, res);
        return res;
    }

    protected int findMethodParameterByName(Parameter[] params, String name) {
        int res = -1;
        for(int i = 0; i < params.length; i++) {
            Parameter p = params[i];
            if (p.isNamePresent()) {
                String paramName = p.getName();
                if (paramName.equals(name)) {
                    res = i;
                    break;
                }
            }
        }
        return res;
    }

    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return "FxFunc:invoke";
    }

}

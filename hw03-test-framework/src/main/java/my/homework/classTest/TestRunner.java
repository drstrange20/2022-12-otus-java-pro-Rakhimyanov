package my.homework.classTest;

import my.homework.annotations.After;
import my.homework.annotations.Before;
import my.homework.annotations.Test;
import my.homework.service.ConsoleOutputService;
import service.OutputService;
import service.ReportService;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TestRunner {
    private final ReportService outputService = new ConsoleOutputService();

    public void run(String className) throws
            ClassNotFoundException,
            NoSuchMethodException,
            InvocationTargetException,
            InstantiationException,
            IllegalAccessException {

        int testFailedCount = 0;
        Object object;

        Class<?> clazz = Class.forName(className);
        Constructor<?> constructor = clazz.getConstructor();

        Method[] allMethods = clazz.getDeclaredMethods();
        List<Method> beforeAnnotationMethods = getMethodsByAnnotation(Before.class, allMethods);
        List<Method> testAnnotationMethods = getMethodsByAnnotation(Test.class, allMethods);
        List<Method> afterAnnotationMethods = getMethodsByAnnotation(After.class, allMethods);

        for (Method method : testAnnotationMethods) {
            object = constructor.newInstance();
            try {
                callMethods(beforeAnnotationMethods, object);
                callMethods(Collections.singletonList(method), object);
            } catch (Exception e) {
                testFailedCount++;
            } finally {
                callMethods(afterAnnotationMethods, object);
            }
        }
        outputService.printReport(String.valueOf(testAnnotationMethods.size()), String.valueOf(testFailedCount));
    }

    private void callMethods(List<Method> methods, Object object) throws InvocationTargetException, IllegalAccessException {
        for (Method method : methods) {
            method.invoke(object);
        }
    }

    private List<Method> getMethodsByAnnotation (Class<? extends Annotation> annotation, Method[] allMethods) {
        return Arrays.stream(allMethods)
               .filter(method -> method.isAnnotationPresent(annotation))
               .collect(Collectors.toList());
    }
}
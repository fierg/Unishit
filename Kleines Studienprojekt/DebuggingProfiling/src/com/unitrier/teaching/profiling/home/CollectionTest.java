package com.unitrier.teaching.profiling.home;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import static java.lang.System.out;

public final class CollectionTest
{
    private enum CollectionTypeEnum
    {
        LinkedList,
        ArrayList,
        Vector
        //TODO
    }

    private static List<Long> runCollectionTest(CollectionTypeEnum collectionType, int len, long repetitions, double part)
    {
        List<Integer> list;
        switch (collectionType)
        {
            case LinkedList:
                list = new LinkedList<>();
                break;
            case ArrayList:
                list = new ArrayList<>();
                break;
            //TODO
            case Vector:
                list = new Vector<>();
                break;
            default:
                throw new IllegalArgumentException("Invalid collection type!");
        }


        for (int i = 0; i < len; i++)
        {
            list.add(i);
        }

        List<Long> times = new LinkedList<>();


        if(repetitions < 0){
            repetitions = getEstimatedRepetitions(len, list);
        }

        System.out.printf("Using\t %s\tRepetitions: %d\n",
                collectionType.toString(), repetitions);



        do
        {
            long start = System.currentTimeMillis();

            int index = (int) (Math.random() * len);
            list.add(list.remove(index));

            times.add(System.currentTimeMillis()-start);

        } while (--repetitions > 0);


        return times;
    }

    private static long getEstimatedRepetitions(int len, List<Integer> list) {
        long repetitions = 0;
        long start = System.currentTimeMillis();
        LinkedList<Long> test = new LinkedList<>();

        do
        {

            int index = (int) (Math.random() * len);
            list.add(list.remove(index));
            test.add(System.currentTimeMillis());
            repetitions++;

        } while (System.currentTimeMillis() - start < 2000);
        return repetitions*2;
    }

    public static void main(String[] args)
    {
        out.println("\ntestCollection()\n");
        testCollection();

        out.println("\nprofiling() withour part\n");
            profiling(1);

        out.println("\nprofiling() with part\n");

    }

    private static void testCollection() {
        CollectionTypeEnum[] values = CollectionTypeEnum.values();
        int listLength = 100;
        long repetitions = 10000000;

        CollectionTypeEnum collectionType = values[2];

        List<Long> times = runCollectionTest(collectionType, listLength, repetitions, 1D);

        long totalNanos = times.stream().mapToLong(Long::longValue).sum();

        int size = times.size();

        out.printf("%s: repetitions(size) = %d, part = %f, total nanos = %d," +
                        " total millis = %f, average nanos = %f, average millis = %fms\n",
                collectionType.toString(), size, 1D, totalNanos,
                totalNanos / 1E6, (double) totalNanos / size, (double) totalNanos / (1E6 * size));
    }

    public static void profiling(double part){
        int listLength = 1000;
        for (CollectionTypeEnum collectionType: CollectionTypeEnum.values()) {

            List<Long> times = runCollectionTest(collectionType, listLength,-1, part);

            long totalNanos = times.stream().mapToLong(Long::longValue).sum();

            int size = times.size();

            out.printf("%s: repetitions(size) = %d, part = %f, total nanos = %d," +
                            " total millis = %f, average nanos = %f, average millis = %fms\n",
                    collectionType.toString(), size, 1D, totalNanos,
                    totalNanos / 1E6, (double) totalNanos / size, (double) totalNanos / (1E6 * size));
        }

        }
    }

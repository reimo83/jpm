package com.jpm.sales.processor;

/**
 * Keeps data processing in progress
 */
public class SalesProcessor
{
  public static void main(String[] args) throws InterruptedException
  {
    ProcessInput processor = new ProcessInput(
      "input/",
      "processed/"
    );

    while (1 == 1)
    {
      processor.processInputFiles();

      System.out.print(".");
      Thread.sleep(5000);
    }
  }
}

package com.jpm.sales.processor;
/**
 * Data structure for holding product specific values.
 *
 */
public class Product
{
  private Integer totalQuantity = 0;
  private Float totalPrice = new Float(0);

  /**
   *
   * @param quantity Number of items
   * @param priceOfItem Price of the item
   */
  public Product(Integer quantity, Float priceOfItem)
  {
    this.add(quantity, priceOfItem);
  }

  /**
   *
   * @param quantity Number of items
   * @param priceOfItem Price of the item
   */
  public void add(Integer quantity, Float priceOfItem)
  {
    this.totalQuantity += quantity;
    this.totalPrice += priceOfItem;
  }

  public Integer getTotalQuantity()
  {
    return this.totalQuantity;
  }

  public Float getTotalPrice()
  {
    return this.totalPrice;
  }
}

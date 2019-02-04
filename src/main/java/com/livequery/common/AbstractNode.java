package com.livequery.common;

public abstract class AbstractNode extends Thread implements INode {

  /**
   * Current thread
   */
  private final Thread currentThread;

  public AbstractNode() {
    this.currentThread = this;
  }

  @Override
  public void start() {
    pre();
    super.start();
  }

  @Override
  public void terminate() {
    post();
  }

  /**
   * Pre start hook which subclasses will need to override
   */
  protected abstract void pre();

  /**
   * Post shutdown hook which subclasses will need to override
   */
  protected abstract void post();
}

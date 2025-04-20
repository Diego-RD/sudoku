package model;

public enum GameStatusEnum {

  NOW_STARTED("não iniciado"),
  INCOMPLETE("incompleto"),
  COMPLETE("completo");

 private String label;

 GameStatusEnum(final String label){
   this.label = label;
 }

  public String getLabel() {
    return label;
  }
}

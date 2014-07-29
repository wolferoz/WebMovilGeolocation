<?php

class Application_Model_Voto
{
  private $_table;

function __construct(){
    $this->_table=new Application_Model_DbTable_Voto();

}

public function guardar($id_voto,$afavor,$encontra,$id_post)
{
if ($id_post==0)
{
    $row=  $this->_table->createRow();
}else
{
    $row=  $this->_table->find($id_post)->current();
}
//$row->id_personal=$persona->getId();
//date_default_timezone_set('America/Lima');

$row->afavor=$afavor;
$row->encontra=$encontra;
$row->id_post=$id_post;

$row->save();
}


}


<?php

class Application_Model_DbTable_Regiones extends Zend_Db_Table_Abstract
{

    protected $_name = 'regiones';


public function All($Id)
{
    $db=$this->getAdapter();
    $select=$db->select();
    $select->from(array('a'=>$this->_name));
    $select->where('a.id_pais=?',$Id);
    return $db->fetchAll($select);
}
}
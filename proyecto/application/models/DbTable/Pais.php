<?php

class Application_Model_DbTable_Pais extends Zend_Db_Table_Abstract
{

    protected $_name = 'pais';


public function All($Id)
{
    $db=$this->getAdapter();
    $select=$db->select();
    $select->from(array('a'=>$this->_name));
    $select->where('a.id_pais=?',$Id);
    $select->order(array('a.id_post DESC'));
    return $db->fetchAll($select);
}
}
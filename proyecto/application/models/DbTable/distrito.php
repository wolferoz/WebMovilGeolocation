<?php

class Application_Model_DbTable_Distrito extends Zend_Db_Table_Abstract
{

    protected $_name = 'distrito';


public function All($Id)
{
    $db=$this->getAdapter();
    $select=$db->select();
    $select->from(array('a'=>$this->_name));
    $select->where('a.id_provincia=?',$Id);
    return $db->fetchAll($select);
}
}
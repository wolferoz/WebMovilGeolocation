<?php

class Application_Model_DbTable_Voto extends Zend_Db_Table_Abstract
{

    protected $_name = 'voto';


public function All($Id)
{
    $db=$this->getAdapter();
    $select=$db->select();
    $select->from(array('a'=>$this->_name));
    $select->where('a.id_post=?',$Id);
    return $db->fetchAll($select);
}
}
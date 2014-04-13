#!/usr/bin/env python
import sqlite3

class DB(object):
    def __init__(self, db):
        self.conn = sqlite3.connect(db)
        self.c = self.conn.cursor()

    def new_table(self, tab, cols, rm_old=True):
        if rm_old:
            col_str = '('+', '.join(cols)+')'
            self.c.execute('drop table if exists '+tab)
            self.c.execute('create table '+tab+' '+col_str)
        else:
            col_str = '('+', '.join(cols)+')'
            self.c.execute('create table if not exists'+tab+' '+col_str)
        self.conn.commit()

    def clone_from(self, db, tab, rm_old=True):
        cols = db.columns(tab)
        self.new_table(tab, cols, rm_old)
        for record in db.queryall(tab):
            self.insert(record, tab)
        self.commit()
    
    def columns(self, tab):
        self.c.execute('select * from %s'%tab)
        return [i[0] for i in self.c.description]
            

    def query(self, targets, tab, rule=None):
        cols = ', '.join(targets)
        if rule == None:
            sql = 'select %s from %s' % (cols, tab)
        else:
            sql = 'select %s from %s where %s' % (cols, tab, rule)
        self.c.execute(sql)
        return self.c.fetchall()

    def queryone(self, target, tab, rule=None):
        return [item[0] for item in self.query([target], tab, rule)]

    def queryall(self, tab, rule=None):
        return self.query(['*'], tab, rule)

    def insert(self, row, tab, commit=False):
        param = ', '.join(['?']*len(row))
        sql = 'insert into %s values (%s)'%(tab, param)
        self.c.execute(sql,row)
        if commit:
            self.conn.commit()

    def insertmany(self, rows, tab, commit=False):
        for row in rows:
            param = ', '.join(['?']*len(row))
            sql = 'insert into %s values (%s)'%(tab, param)
            self.c.execute(sql,row)
        if commit:
            self.conn.commit()

    def commit(self):
        self.conn.commit()

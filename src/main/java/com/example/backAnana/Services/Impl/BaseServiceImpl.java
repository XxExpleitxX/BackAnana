package com.example.backAnana.Services.Impl;

import com.example.backAnana.Entities.Base;
import com.example.backAnana.Repositories.BaseRepository;
import com.example.backAnana.Services.BaseService;
import jakarta.transaction.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public abstract class BaseServiceImpl<T extends Base, ID extends Serializable> implements BaseService<T, ID> {

    protected BaseRepository<T, ID> baseRepository;

    public BaseServiceImpl(BaseRepository<T, ID> baseRepository) {
        this.baseRepository = baseRepository;
    }

    @Override
    @Transactional
    public List<T> findAll() throws Exception{
        try{
            return baseRepository.findAll();
        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Transactional
    public T findById(ID id) throws Exception{
        try{
            Optional<T> entity = baseRepository.findById(id);
            return entity.get();
        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Transactional
    public T save(T entity) throws Exception{
        try{
            entity = baseRepository.save(entity);
            return entity;
        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Transactional
    public T update(T entity) throws Exception{
        try{
            if(entity.getId() == null){
                throw new Exception("La entidad a modificar no fue encontrada.");
            }
            return baseRepository.save(entity);
        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean delete(ID id) throws Exception{
        try{
            Optional<T> entity = baseRepository.findById(id);
            if(entity.isPresent()){
                baseRepository.delete(entity.get());
                return true;
            }else{
                throw new Exception("La entidad a eliminar no fue encontrada");
            }
        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }

}

package com.example.backAnana.Services;

import com.example.backAnana.Entities.Config;

public interface ConfigService extends BaseService<Config, Long> {

    double getDescuentoGlobal();
    void setDescuentoGlobal(double descuento);

}

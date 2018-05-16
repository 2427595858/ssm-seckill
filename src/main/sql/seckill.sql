-- 秒杀执行的存储过程
-- 在商业数据库应用中，例如金融、企业、政府等等，存储过程的使用非常广泛
-- 而在互联网行业，存储过程很少使用，一个重要的原因是MySQL的广泛使用，
-- 而MySQL的存储过程的功能很弱（跟商业数据库相比）；另外也跟互联网行业变化快有一定的关系。

DELIMITER $$  -- 将命令行结束符  ; 转换为 $$
-- 定义存储过程
CREATE PROCEDURE `seckill`.`execute_seckill`
  (in v_seckill_id bigint,in v_user_phone bigint,
    in v_kill_time timestamp,out r_result int)
  BEGIN
    DECLARE insert_count int DEFAULT 0;
    START TRANSACTION ;
    INSERT ignore INTO success_killed
      VALUES (v_seckill_id,v_user_phone,0,v_kill_time);
    SELECT ROW_COUNT() INTO insert_count;
    IF(insert_count < 0) THEN
      ROLLBACK ;
      SET r_result = -2;            -- 系统内部错误
    ELSEIF(insert_count = 0) THEN
      ROLLBACK ;
      SET r_result = -1;            -- 重复秒杀
    ELSE
      UPDATE seckill
      SET number = number-1
      WHERE seckill_id = v_seckill_id
              AND end_time > v_kill_time
              AND start_time < v_kill_time
              AND number > 0;
      SELECT ROW_COUNT() INTO insert_count;
      IF(insert_count = 0) THEN
        ROLLBACK ;
        SET r_result = 0;           -- 表示秒杀结束
      ELSEIF(insert_count < 0) THEN
        ROLLBACK ;
        SET r_result = -2;
      ELSE
        COMMIT ;
        SET r_result = 1;           -- 插入成功
      END IF ;
    END IF ;
  END ;
$$
-- $$ 存储过程定义结束

DELIMITER ;
-- 定义变量
SET @r_result=-3;
call execute_seckill(1,13488986623,now(),@r_result);

-- 获取结果
SELECT @r_result

-- 视情况使用存储过程
-- 经过测试，大约可以做到一个秒杀单 6000/QPS
package com.example.demo.domain.model;


import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "otp")
public class Otp {
  // https://rightcode.co.jp/blogs/41189

  // Spring Data JPAではsaveメソッドを実行すると
  // 更新対象のキーでselect
  // 同じキーのレコードがあるかどうかチェック
  // あったらupdate
  // なかったらinsert
  // という順で処理してるっぽいです

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String mail;

  private String code;

  @Column(name = "expired_at")
  private LocalDateTime expiredAt;

  @Column(name = "send_times")
  private int sendTimes;

  @Column(name = "first_send_at")
  private LocalDateTime firstSendAt;

  @Column(name = "failed_times")
  private int failedTimes;

  @Column(name = "is_used")
  private int isUsed;

  // @Column(name = "created_at")
  @Column(name = "created_at", updatable = false)
  // 「updatable = false」・・・更新されない
  // 一度設定したら変更されないフィールドに対して有効
  @CreatedDate
  // 「@CreatedDate」・・・Spring Data JPA の監査機能を使用してエンティティの作成日時を自動的に設定※監査設定を有効にする必要あり
  private LocalDateTime createdAt;

  @Column(name = " updated_at")
  @LastModifiedDate
  private LocalDateTime updatedAt;

  // // Hooks for lifecycle events
  // @PrePersist
  // // エンティティがデータベースに新規に挿入される前に下記のメソッドを実行
  // protected void onCreate() {
  // createdAt = LocalDateTime.now();
  // updatedAt = LocalDateTime.now();
  // }

  // @PreUpdate
  // // エンティティがデータベースに対して更新される前に下記のメソッドを実行
  // protected void onUpdate() {
  // updatedAt = LocalDateTime.now();
  // }

}

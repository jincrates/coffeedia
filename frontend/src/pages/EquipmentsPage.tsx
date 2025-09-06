import React from 'react';
import { Plus, Settings } from 'lucide-react';
import Button from '@/components/common/Button';
import Card, { CardContent } from '@/components/common/Card';

const EquipmentsPage: React.FC = () => {
  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Header */}
        <div className="mb-8">
          <div className="flex justify-between items-center mb-6">
            <div>
              <h1 className="text-2xl font-bold text-gray-900">장비 관리</h1>
              <p className="text-gray-600 mt-1">
                나만의 커피 장비를 등록하고 관리해보세요
              </p>
            </div>
            <Button
              leftIcon={<Plus className="h-4 w-4" />}
              onClick={() => alert('장비 추가 기능은 곧 구현됩니다!')}
            >
              장비 추가
            </Button>
          </div>
        </div>

        {/* Empty State */}
        <Card className="text-center py-12">
          <CardContent>
            <Settings className="h-24 w-24 text-gray-300 mx-auto mb-4" />
            <h3 className="text-lg font-medium text-gray-900 mb-2">
              아직 등록된 장비가 없습니다
            </h3>
            <p className="text-gray-500 mb-6">
              첫 번째 장비를 추가해서 나만의 커피 장비를 관리해보세요!
            </p>
            <Button
              leftIcon={<Plus className="h-4 w-4" />}
              onClick={() => alert('장비 추가 기능은 곧 구현됩니다!')}
            >
              첫 장비 추가하기
            </Button>
          </CardContent>
        </Card>
      </div>
    </div>
  );
};

export default EquipmentsPage;

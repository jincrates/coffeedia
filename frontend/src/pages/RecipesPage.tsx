import React from 'react';
import {Book, Plus} from 'lucide-react';
import Button from '@/components/common/Button';
import Card, {CardContent} from '@/components/common/Card';
import toast from "react-hot-toast";

const RecipesPage: React.FC = () => {
  return (
      <div className="min-h-screen bg-gray-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          {/* Header */}
          <div className="mb-8">
            <div className="flex justify-between items-center mb-6">
              <div>
                <h1 className="text-2xl font-bold text-gray-900">레시피 북</h1>
                <p className="text-gray-600 mt-1">
                  나만의 커피 레시피를 저장하고 관리해보세요
                </p>
              </div>
              <Button
                  leftIcon={<Plus className="h-4 w-4"/>}
                  onClick={() => toast('레시피 추가 기능은 곧 구현됩니다!')}
              >
                레시피 추가
              </Button>
            </div>
          </div>

          {/* Empty State */}
          <Card className="text-center py-12">
            <CardContent>
              <Book className="h-24 w-24 text-gray-300 mx-auto mb-4"/>
              <h3 className="text-lg font-medium text-gray-900 mb-2">
                아직 등록된 레시피가 없습니다
              </h3>
              <p className="text-gray-500 mb-6">
                첫 번째 레시피를 추가해서 나만의 레시피 북을 만들어보세요!
              </p>
              <Button
                  leftIcon={<Plus className="h-4 w-4"/>}
                  onClick={() => toast('레시피 추가 기능은 곧 구현됩니다!')}
              >
                첫 레시피 추가하기
              </Button>
            </CardContent>
          </Card>
        </div>
      </div>
  );
};

export default RecipesPage;
